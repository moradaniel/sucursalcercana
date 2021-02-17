package com.gis.sucursales.model;

public class Longitud {

    double longitud;

    public Longitud(double longitud){
        if(longitud < -180 || longitud > 180) {
            throw new IllegalArgumentException("Longitud debe ser entre -180 y +180 grados");
        }
        this.longitud = longitud;

    }

    public double getLongitud(){
        return longitud;
    }

}
