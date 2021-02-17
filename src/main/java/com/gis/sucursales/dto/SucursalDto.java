package com.gis.sucursales.dto;

import com.gis.sucursales.model.Latitud;
import com.gis.sucursales.model.Longitud;
import com.gis.sucursales.model.Sucursal;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Objects;

public class SucursalDto {

	private Long id;
	private String direccion;
	@Min(-180)
	@Max(180)
	private Double longitud;
	@Min(-90)
	@Max(90)
	private Double latitud;


	public SucursalDto() {
	}

	public SucursalDto(Long id, String direccion, Double longitud, Double latitud) {
		this.id = id;
		this.direccion = direccion;
		this.longitud = longitud;
		this.latitud = latitud;
	}

	public SucursalDto(String direccion, Double latitud, Double longitud) {
		this(null,direccion,latitud,longitud);
	}

	public Long getId() {
		return id;
	}

	public String getDireccion() {
		return direccion;
	}

	public Double getLatitud() {
		return latitud;
	}

	public Double getLongitud() {
		return longitud;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public void setLatitud(Double latitud) {
		this.latitud = latitud;
	}

	public void setLongitud(Double longitud) {
		this.longitud = longitud;
	}


	public static SucursalDto of(Sucursal sucursal) {
		return new SucursalDto(sucursal.getId(),
				sucursal.getDireccion(),
				sucursal.getLongitud().getLongitud(),
				sucursal.getLatitud().getLatitud()
				);
	}

	public static Sucursal of(SucursalDto sucursalDto) {
		return new Sucursal(sucursalDto.getId(),
				sucursalDto.getDireccion(),
				new Longitud(sucursalDto.getLongitud()),
				new Latitud(sucursalDto.getLatitud()));
	}

	@Override
	public String toString() {
		return new org.apache.commons.lang3.builder.ToStringBuilder(this)
				.append("id", id)
				.append("direccion", direccion)
				.append("longitud", longitud)
				.append("latitud", latitud)
				.toString();
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SucursalDto that = (SucursalDto) o;
		return Objects.equals(id, that.id) && Objects.equals(direccion, that.direccion) && Objects.equals(longitud, that.longitud) && Objects.equals(latitud, that.latitud);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, direccion, longitud, latitud);
	}
}
