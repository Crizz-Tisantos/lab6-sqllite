package com.example.ubicacion_proyecto;

public class Ubicacion {

    Double longitud,latitud;
    String fecha,ubicacionid;
    String hora;
    String direccion;
    public Ubicacion(String ubicacionid, Double longitud, Double latitud, String fecha, String hora, String direccion) {
        this.longitud = longitud;
        this.latitud = latitud;
        this.fecha = fecha;
        this.hora = hora;
        this.direccion = direccion;
    }

    public Double getLongitud() {
        return longitud;
    }

    public Double getLatitud() {
        return latitud;
    }

    public String getFecha() {
        return fecha;
    }

    public String getHora() {
        return hora;
    }

    public String getDireccion() {
        return direccion;
    }
}
