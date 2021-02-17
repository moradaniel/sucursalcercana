package com.gis.sucursales.integration;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gis.sucursales.dto.ApiResponse;
import com.gis.sucursales.dto.SucursalDto;
import com.gis.sucursales.model.Latitud;
import com.gis.sucursales.model.Longitud;
import com.gis.sucursales.model.Sucursal;
import com.gis.sucursales.repository.SucursalRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.sql.DataSource;
import java.io.File;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SucursalServiceIntegrationTest {

    @Autowired
    DataSource dataSource;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SucursalRepository sucursalRepository;

    File DATA_JSON = Paths.get("src", "test", "resources", "sucursales.json").toFile();

    Map<Long, Sucursal> mapSucursal = new HashMap<>();

    ObjectMapper mapper = new ObjectMapper();
    JavaType apiResponseType;

    public SucursalServiceIntegrationTest() throws Exception {
        apiResponseType = mapper.getTypeFactory().constructParametricType(ApiResponse.class, SucursalDto.class);
    }

    @BeforeEach
    public void setup() throws Exception {

        cleanTestDatabase();

        // Deserialize sucursales from JSON file to Sucursal array
        SucursalDto[] sucursalDtos = new ObjectMapper().readValue(DATA_JSON, SucursalDto[].class);

        // Save each sucursal to database
        Arrays.stream(sucursalDtos)
                .map(SucursalDto::of)
                .forEach((sucursal)->{
                    Sucursal savedSucursal = sucursalRepository.save(sucursal);
                    mapSucursal.put(savedSucursal.getId(),savedSucursal);
                });
    }

    @AfterEach
    public void cleanup(){
        // Cleanup the database after each test
        sucursalRepository.deleteAll();
    }

    @Test
    @DisplayName("Test sucursal found - GET /sucursales/1")
    public void testGetSucursalByIdFindsSucursal() throws Exception {
        // Perform GET request
        MvcResult mvcResult = mockMvc.perform(get("/sucursales/{id}", 1))
                // Validate 200 OK and JSON response type received
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                .andReturn();
        String json = mvcResult.getResponse().getContentAsString();

        ApiResponse<SucursalDto> apiResponse = mapper.readValue(json, apiResponseType);

        assertThat(apiResponse.getResponse()).isEqualToComparingFieldByField(SucursalDto.of(mapSucursal.get(1l)));

    }

    @Test
    @DisplayName("Add a new Sucursal - POST /sucursales Exitosp")
    public void testAddNewSucursal() throws Exception {
        // Prepare Sucursal to save
        Sucursal newSucursal = new Sucursal("Direccion 1", new Longitud(8), new Latitud(1));

        // Perform POST request
        MvcResult mvcResult =  mockMvc.perform(post("/sucursales")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(new ObjectMapper().writeValueAsString(SucursalDto.of(newSucursal))))

                // Validate 201 CREATED and JSON response type received
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

                // Validate response headers
                .andExpect(header().string(HttpHeaders.LOCATION, "/sucursales/3"))

                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();

        ApiResponse<SucursalDto> apiResponse = mapper.readValue(json, apiResponseType);

        assertThat(apiResponse.getResponse()).isEqualToIgnoringGivenFields(SucursalDto.of(newSucursal), "id");

    }

    @Test
    @DisplayName("Add a new Sucursal con datos invalidos- POST /sucursales Falla")
    public void testAddNewSucursalInvalido() throws Exception {
        // Prepare Sucursal to save
        String newSucursal = "{ \"direccion\":null, \"longitud\":\"-190\", \"latitud\":\"95\" }";

        // Perform POST request
        MvcResult mvcResult =  mockMvc.perform(post("/sucursales")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(newSucursal))

                // Validate bad request
                .andExpect(status().isBadRequest())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();

    }


    @Test
    @DisplayName("Test sucursal mas cercana - GET /sucursales/sucursalmascercana Exitoso")
    public void testGetSucursalMasCercanaACoordenadaDadaExitoso() throws Exception {
        // Perform GET request
        MvcResult mvcResult = mockMvc.perform(get("/sucursales/sucursalmascercana")
                .param("longitud", "2")
                .param("latitud", "4")
                )
                // Validate 200 OK and JSON response type received
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        String json = mvcResult.getResponse().getContentAsString();

        ApiResponse<SucursalDto> apiResponse = mapper.readValue(json, apiResponseType);
        assertThat(apiResponse.getResponse().getDireccion()).isEqualTo("Cordoba Capital");


    }

    @Test
    @DisplayName("Test sucursal mas cercana coordenadas invalidas- GET /sucursales/sucursalmascercana Falla")
    public void testGetSucursalMasCercanaACoordenadaDadaFalla() throws Exception {
        // Perform GET request
        MvcResult mvcResult = mockMvc.perform(get("/sucursales/sucursalmascercana")
                .param("longitud", "200")
                .param("latitud", "-114")
        )
                // Validate
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn() ;

    }

    private void cleanTestDatabase() throws Exception{
        Connection dbConnection = null;
        try{
            dbConnection = dataSource.getConnection();
            //clean test database before each test
            ScriptUtils.executeSqlScript(dbConnection,
                    new ClassPathResource("sql/clean_db_schema.sql"));

        } catch (SQLException e ) {
            throw new RuntimeException(e);
        } finally {
            try { if (dbConnection != null) dbConnection.close(); } catch (Exception e) {};
        }
    }

}
