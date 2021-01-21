package com.example.ubicacion_proyecto;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UbicacionDatadaseHelper extends SQLiteOpenHelper {
    public  static  final  String DATABASE_NAME="Ubicaion.db";
    public  static  final  String TABLE_NAME="Ubicaion_user_table";
    public  static  final  String Col_1="ID";
    public  static  final  String Col_2="FECHA";
    public  static  final  String Col_3="HORA";
    public  static  final  String Col_4="LATITUD";
    public  static  final  String Col_5="LONGITUD";
    public  static  final  String Col_6="DIRECCION";

    private static final String BD_NAME="Ubicacion";
    private static final int version=1;




    public UbicacionDatadaseHelper(Context context) {
        super(context, BD_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+TABLE_NAME+"(ID INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "FECHA TEXT, HORA TEXT, LATITUD REAL, LONGITUD REAL, DIRECCION TEXT)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);

    }

    public void initData(){
        SQLiteDatabase db = this.getWritableDatabase();
     onUpgrade(db, 1, 1);
    }

    public Boolean insertData(Get_Datos get_datos){
        SQLiteDatabase db =this.getWritableDatabase();
        ContentValues contentValues =new ContentValues();
        contentValues.put(Col_2,get_datos.getStrFecha());
        contentValues.put(Col_3,get_datos.getStrHora());
        contentValues.put(Col_4,get_datos.getLatitud());
        contentValues.put(Col_5,get_datos.getLongitud());
        contentValues.put(Col_6,get_datos.getStrDireccion());
        long result=db.insert(TABLE_NAME, null,contentValues);
        if(result==-1)
            return false;
        else
            return true;
    }

    public Cursor getAllData(){
        SQLiteDatabase db =this.getWritableDatabase();
        Cursor result=db.rawQuery("select * from "+TABLE_NAME,null);
        return result;
    }

    public Cursor getData(int id){
        SQLiteDatabase db =this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from "+ TABLE_NAME+"where id="+id+"",null);
        return res;
    }
    public Cursor findDataByEmail(String fecha){
        SQLiteDatabase db =this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from "+ TABLE_NAME+"where "+Col_4+"= '"+fecha+"'",null);
        return res;
    }
}
