package com.gis.sucursales.dto;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class SucursalCreateDtoValidator implements Validator {


    public SucursalCreateDtoValidator() {
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return SucursalDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SucursalDto sucursalDto = (SucursalDto) target;

        if (StringUtils.isEmpty(sucursalDto.getDireccion())) {
            errors.reject("sucursalDto.direccion.is_mandatory", "SucursalDto direccion es requerido");
        }
        if (sucursalDto.getLongitud()==null) {
            errors.reject("sucursalDto.longitud.is_mandatory", "SucursalDto longitud es requerido");
        }
        if (sucursalDto.getLongitud()!=null && (sucursalDto.getLongitud().doubleValue() < -180 || sucursalDto.getLongitud().doubleValue() > 180)) {
            errors.reject("sucursalDto.longitud.error.range", "SucursalDto longitud debe ser entre -180 y 180");
        }

        if (sucursalDto.getLatitud()==null) {
            errors.reject("sucursalDto.latitud.is_mandatory", "SucursalDto latitud es requerido");
        }
        if (sucursalDto.getLatitud()!=null && (sucursalDto.getLatitud().doubleValue() < -90 || sucursalDto.getLatitud().doubleValue() > 90)) {
            errors.reject("sucursalDto.latitud.error.range", "SucursalDto latitud debe ser entre -90 y 90");
        }

    }
}