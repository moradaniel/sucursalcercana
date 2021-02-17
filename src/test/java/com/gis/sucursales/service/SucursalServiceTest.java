package com.gis.sucursales.service;

import com.gis.sucursales.repository.SucursalRepository;
import com.gis.sucursales.model.Latitud;
import com.gis.sucursales.model.Longitud;
import com.gis.sucursales.model.Sucursal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.AssertionErrors;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class SucursalServiceTest {

    @Autowired
    private SucursalService sucursalService;

    @MockBean
    private SucursalRepository sucursalRepository;

    @Test
    @DisplayName("Find Sucursal with id successfully")
    public void testFindSucursalById(){
        Optional<Sucursal> mockSucursal = Optional.of(new Sucursal(1l, "Direccion 1", new Longitud(10), new Latitud(1)));

        doReturn(mockSucursal).when(sucursalRepository).findById(1l);

        Optional<Sucursal> foundSucursal = sucursalService.findById(1l);

        Assertions.assertNotNull(foundSucursal.get());
        Assertions.assertSame(mockSucursal.get().getDireccion(), foundSucursal.get().getDireccion());

    }

    @Test
    @DisplayName("Fail to find Sucursal with id")
    public void testFailToFindSucursalById(){
        doReturn(Optional.empty()).when(sucursalRepository).findById(1l);

        Optional<Sucursal> foundSucursal = sucursalService.findById(1l);

        Assertions.assertFalse(foundSucursal.isPresent());
    }

    @Test
    @DisplayName("Find all sucursales")
    public void testFindAllSucursales(){
        Sucursal firstSucursal = new Sucursal(1l, "Direccion 1", new Longitud(10), new Latitud(1));
        Sucursal secondSucursal = new Sucursal(2l, "Direccion 2", new Longitud(20), new Latitud(2));

        doReturn(Arrays.asList(firstSucursal, secondSucursal)).when(sucursalRepository).findAll();

        Iterable<Sucursal> allSucursales = sucursalService.findAll();

        Assertions.assertEquals(2, ((Collection<?>) allSucursales).size());
    }


    @Test
    @DisplayName("Save new Sucursal successfully")
    public void testSuccessfulSucursalSave(){
        Sucursal mockSucursal = new Sucursal(1l, "Direccion 1", new Longitud(20), new Latitud(2));

        doReturn(mockSucursal).when(sucursalRepository).save(any());

        Sucursal savedSucursal = sucursalService.save(mockSucursal);

        AssertionErrors.assertNotNull("Sucursal should not be null", savedSucursal);
        assertThat(mockSucursal).isEqualToComparingFieldByField(savedSucursal);
    }


    @Test
    @DisplayName("Update an existing Sucursal successfully")
    public void testUpdatingSucursalSuccessfully(){

        Sucursal existingSucursal = new Sucursal(1l, "Direccion 1", new Longitud(20), new Latitud(2));
        Sucursal updatedSucursal = new Sucursal(1l, "Direccion 2", new Longitud(22), new Latitud(3));

        doReturn(Optional.of(existingSucursal)).when(sucursalRepository).findById(1l);
        doReturn(updatedSucursal).when(sucursalRepository).save(any());

        Sucursal updateSucursal = sucursalService.update(existingSucursal);

        Assertions.assertEquals("Direccion 2", updateSucursal.getDireccion());
    }

    @Test
    @DisplayName("Fail to update an existing Sucursal")
    public void testFailToUpdateExistingSucursal(){
        Sucursal mockSucursal = new Sucursal(1l, "Direccion 1", new Longitud(20), new Latitud(2));

        doReturn(Optional.empty()).when(sucursalRepository).findById(1l);

        Sucursal updatedSucursal = sucursalService.update(mockSucursal);

        AssertionErrors.assertNull("Sucursal should be null", updatedSucursal);
    }

}
