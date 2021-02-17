package com.gis.sucursales.repository;

import com.gis.sucursales.model.Sucursal;
import org.locationtech.jts.geom.Point;
import org.springframework.data.repository.query.Param;
import java.util.Optional;


public interface SucursalRepositoryCustom {

    Optional<Sucursal> findSucursalMasCercana(@Param("coordenada") Point coordenada);

}
