package com.gis.sucursales.service;

import com.gis.sucursales.repository.SucursalRepository;
import com.gis.sucursales.model.Latitud;
import com.gis.sucursales.model.Longitud;
import com.gis.sucursales.model.Sucursal;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SucursalService {

    private static final Logger LOGGER = LogManager.getLogger(SucursalService.class);

    @Autowired
    private SucursalRepository sucursalRepository;

    public Sucursal save(Sucursal sucursal){
        LOGGER.info("Saving new sucursal with direccion: {}", sucursal.getDireccion());
        return sucursalRepository.save(sucursal);
    }


    public Sucursal update(Sucursal sucursal){
        LOGGER.info("Updating sucursal with id:{}", sucursal.getId());
        Optional<Sucursal> existingSucursalOptional = sucursalRepository.findById(sucursal.getId());
        Sucursal existingSucursal= null;
        if (existingSucursalOptional.isPresent()){
            existingSucursal = existingSucursalOptional.get();
            Sucursal updatedSucursal = new Sucursal(existingSucursal.getId(),
                    sucursal.getDireccion(),
                    sucursal.getLongitud(),
                    sucursal.getLatitud());

            //existingSucursal.setDireccion(sucursal.getDireccion());

            existingSucursal = sucursalRepository.save(updatedSucursal);
        } else {
            //TODO Entity not found exception
            LOGGER.error("Sucursal with id {} could not be updated!", sucursal.getId());
        }
        return existingSucursal;
    }

    public Optional<Sucursal> findById(Long id){
        LOGGER.info("Finding sucursal by id:{}", id);
        return sucursalRepository.findById(id);
    }


    public void delete(Long id){
        LOGGER.info("Deleting sucursal with id:{}", id);
        Optional<Sucursal> existingSucursal = sucursalRepository.findById(id);
        if (existingSucursal.isPresent()){
            sucursalRepository.delete(existingSucursal.get());
        } else {
            LOGGER.error("Sucursal with id {} could not be found!", id);
        }
    }

    public Iterable<Sucursal> findAll() {
        return sucursalRepository.findAll();
    }

    public Optional<Sucursal> findSucursalMasCercana(Longitud longitud, Latitud latitud) throws Exception{
        return sucursalRepository.findSucursalMasCercana((Point) new WKTReader().read("POINT(" + longitud.getLongitud() + " " + latitud.getLatitud() + ")"));
    }

}
