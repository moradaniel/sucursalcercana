package com.gis.sucursales.repository;

import com.gis.sucursales.model.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface SucursalRepository extends SucursalRepositoryCustom,JpaRepository<Sucursal, Long> {

    Optional<Sucursal> findByDireccion(String direccion);

}