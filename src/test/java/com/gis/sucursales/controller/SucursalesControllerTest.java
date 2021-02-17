package com.gis.sucursales.controller;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gis.sucursales.dto.ApiResponse;
import com.gis.sucursales.dto.SucursalDto;
import com.gis.sucursales.service.SucursalService;
import com.gis.sucursales.model.Latitud;
import com.gis.sucursales.model.Longitud;
import com.gis.sucursales.model.Sucursal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SucursalesControllerTest {

    @MockBean
    private SucursalService sucursalService;

    @Autowired
    private MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();
    JavaType apiResponseType;

    public SucursalesControllerTest() throws Exception {
        apiResponseType = mapper.getTypeFactory().constructParametricType(ApiResponse.class, SucursalDto.class);
    }


    @Test
    @DisplayName("Test Sucursal found - GET /sucursales/1")
    public void testGetSucursalByIdFindsSucursal() throws Exception {
        // Prepare mock Sucursal
        Sucursal mockSucursal = new Sucursal(1l,"Direccion 1", new Longitud(5), new Latitud(1));

        SucursalDto expectedSucursalDto = SucursalDto.of(mockSucursal);

        // Prepare mocked service method
        doReturn(Optional.of(mockSucursal)).when(sucursalService).findById(mockSucursal.getId());

        // Perform GET request
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/sucursales/{id}", 1))
                // Validate 200 OK and JSON response type received
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();

        ApiResponse<SucursalDto> apiResponse = mapper.readValue(json, apiResponseType);

        assertThat(apiResponse.getResponse()).isEqualToIgnoringGivenFields(expectedSucursalDto, "id" );

    }

    @Test
    @DisplayName("Add a new Sucursal - POST /sucursales")
    public void testAddNewSucursal() throws Exception {
        // Prepare mock Sucursal
        Sucursal newSucursal = new Sucursal("Direccion 1", new Longitud(8),new Latitud(1));
        Sucursal mockSucursal = new Sucursal(1l, "Direccion 1", new Longitud(8),new Latitud(1));
        SucursalDto expectedSucursalDto = SucursalDto.of(mockSucursal);

        // Prepare mock service method
        doReturn(mockSucursal).when(sucursalService).save(ArgumentMatchers.any());

        // Perform POST request
        MvcResult mvcResult = mockMvc.perform(post("/sucursales")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(new ObjectMapper().writeValueAsString(SucursalDto.of(newSucursal))))

                // Validate 201 CREATED and JSON response type received
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate response headers
                .andExpect(header().string(HttpHeaders.LOCATION, "/sucursales/1"))

                .andReturn();


        String json = mvcResult.getResponse().getContentAsString();

        ApiResponse<SucursalDto> apiResponse = mapper.readValue(json, apiResponseType);

        assertThat(apiResponse.getResponse()).isEqualToComparingFieldByField(expectedSucursalDto);


    }

}
