package com.gis.sucursales.model;

public class Latitud {

    double latitud;

    public Latitud(double latitud){
        if(latitud < -90 || latitud > 90) {
            throw new IllegalArgumentException("Latitud debe ser entre -90 y +90 grados");
        }
        this.latitud = latitud;

    }

    public double getLatitud(){
        return latitud;
    }
}
