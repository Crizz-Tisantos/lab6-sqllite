package com.example.ubicacion_proyecto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Get_Datos<DatabaseHelper> extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private LocationManager locationManager;
    private Location loc;
    private UbicacionDatadaseHelper myBD;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    private String strFecha, strHora, strDireccion;
    private double longitud, latitud;

    private DatabaseReference Ubicacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get__datos);
        Ubicacion = FirebaseDatabase.getInstance().getReference();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        fechaHora();
        try {
            ubicacion2();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void fechaHora() {
        final TextView txtFecha = (TextView) findViewById(R.id.fechaTxt);
        final TextView txtHora = (TextView) findViewById(R.id.hora2);
        Date date = new Date();
        DateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        strHora = hourFormat.format(date);
        strFecha = dateFormat.format(date);
        txtHora.setText(strHora);
        txtFecha.setText(strFecha);
    }


    private void ubicacion2() throws IOException {
        final TextView txtLatitud = (TextView) findViewById(R.id.latitudTxt);
        final TextView txtLongitud = (TextView) findViewById(R.id.longitudTxt);
        final TextView txtDireccion = (TextView) findViewById(R.id.ubicacionTxt);

        ActivityCompat.requestPermissions(Get_Datos.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            txtLatitud.setText("denegados.");
            txtLongitud.setText("");

            return;
        } else {

            txtLatitud.setText("Aprobados");
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (loc != null) {
                txtLatitud.setText(String.valueOf(loc.getLatitude()));
                ;
                txtLongitud.setText(String.valueOf(loc.getLongitude()));

            } else {
                txtLatitud.setText("no hay ultima poscicion");
            }
        }

        latitud = loc.getLatitude();
        longitud = loc.getLongitude();


        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());


        addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName();
        txtDireccion.setText(address);
        strDireccion = address;


// SQLite
        myBD = new UbicacionDatadaseHelper(this);
        addData();
        ViewAll();


        String id = Ubicacion.push().getKey();
        Ubicacion Lugar = new Ubicacion(id, loc.getLongitude(), loc.getLatitude(), "20/01/2021", "7:15", address);

        Map<String, Object> Contactos = new HashMap<>();
        Contactos.put("Fecha", "20/01/2021");
        Contactos.put("Hora", "7:39");
        Contactos.put("Latitud", loc.getLatitude());
        Contactos.put("Longitud", loc.getLongitude());
        Contactos.put("Dirreccion", address);


//Firebase
        db.collection("Contactos")
                .add(Contactos)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //    Log.w(TAG, "Error adding document", e);
                    }
                });


    }
//SQLite
    private void ViewAll() {
        Cursor res=myBD.getAllData();
       StringBuffer buffer=new StringBuffer();
       while (res.moveToNext()){
           buffer.append("id"+res.getString(0)+"\n");
           buffer.append("FECHA"+res.getString(1)+"\n");
           buffer.append("HORA"+res.getString(2)+"\n");
           buffer.append("LATITUD"+res.getString(3)+"\n");
           buffer.append("LONGITUD"+res.getString(4)+"\n");
           buffer.append("DIRECCION"+res.getString(5)+"\n");

           showMessage("data",buffer.toString());


       }
    }
    //SQLite
    private void showMessage(String title, String message) {
        AlertDialog.Builder builder =new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();

    }
    //SQLite
    private void addData() {
        final TextView txtDireccion = (TextView) findViewById(R.id.ubicacionTxt);
        Get_Datos datos= new Get_Datos();
        datos.setStrFecha(strFecha);
        datos.setStrHora(strHora);
        datos.setLatitud(latitud);
        datos.setLongitud(longitud);
        datos.setStrDireccion(strDireccion);
        boolean isInserted=myBD.insertData(datos);
        if(isInserted){
            txtDireccion.setText("guardado");
        }else{
            txtDireccion.setText("no guardado");
        }


    }


    public String getStrFecha() {
        return strFecha;
    }

    public String getStrHora() {
        return strHora;
    }

    public String getStrDireccion() {
        return strDireccion;
    }

    public double getLongitud() {
        return longitud;
    }

    public double getLatitud() {
        return latitud;
    }

    public DatabaseReference getUbicacion() {
        return Ubicacion;
    }

    public void onClickObtDatos(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void setStrFecha(String strFecha) {
        this.strFecha = strFecha;
    }

    public void setStrHora(String strHora) {
        this.strHora = strHora;
    }

    public void setStrDireccion(String strDireccion) {
        this.strDireccion = strDireccion;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public void setUbicacion(DatabaseReference ubicacion) {
        Ubicacion = ubicacion;
    }
}