package com.gis.sucursales.model;

import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTReader;

import javax.persistence.*;
import java.util.Objects;


@Entity
@Table(name = "sucursal")
public class Sucursal {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue
    private Long id;
    @Column(unique = true)
    private String direccion;
    @Column(name = "coordenadas")
    private Point coordenadas;


    public Sucursal() {
    }

    public Sucursal(Long id, String direccion, Longitud longitud, Latitud latitud) {
        this.id = id;
        this.direccion = direccion;

        try {
            coordenadas = (Point) new WKTReader().read("POINT(" + longitud.getLongitud() + " " + latitud.getLatitud() + ")");
        }
        catch(Exception e){
            new RuntimeException(e);
        }
    }

    public Sucursal(String direccion, Longitud longitud, Latitud latitud) {
        this(null, direccion,longitud,latitud);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Latitud getLatitud() {
        return new Latitud(coordenadas.getY());
    }

    public Longitud getLongitud() {
        return new Longitud(coordenadas.getX());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sucursal sucursal = (Sucursal) o;
        return Objects.equals(id, sucursal.id) && Objects.equals(direccion, sucursal.direccion) && Objects.equals(coordenadas, sucursal.coordenadas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, direccion, coordenadas);
    }
}
