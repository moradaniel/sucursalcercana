package com.gis.sucursales.repository;

import com.gis.sucursales.model.Sucursal;
import org.locationtech.jts.geom.Point;
import org.springframework.data.repository.query.Param;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Optional;


public class SucursalRepositoryImpl implements SucursalRepositoryCustom {


    @PersistenceContext
    private EntityManager entityManager;

    public Optional<Sucursal> findSucursalMasCercana(@Param("coordenada") Point coordenada){

         String queryString = "SELECT suc FROM Sucursal suc order by distance (suc.coordenadas, :coordenada) asc";
         Query query = entityManager.createQuery( queryString);
         query.setParameter("coordenada", coordenada);
         return query.getResultList().stream().findFirst();
     }


}