package com.gis.sucursales.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gis.sucursales.dto.SucursalDto;
import com.gis.sucursales.model.Latitud;
import com.gis.sucursales.model.Longitud;
import com.gis.sucursales.model.Sucursal;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.io.File;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith({SpringExtension.class})
@SpringBootTest
public class SucursalRepositoryTests {

    @Autowired
    DataSource dataSource;

    @Autowired
    private SucursalRepository sucursalRepository;

    private static File DATA_JSON = Paths.get("src", "test", "resources", "sucursales.json").toFile();

    @BeforeEach
    public void setup() throws Exception {
        cleanTestDatabase();

        // Deserialize sucursalDtos from JSON file to Sucursal array
        SucursalDto[] sucursalDtos = new ObjectMapper().readValue(DATA_JSON, SucursalDto[].class);

        Arrays.stream(sucursalDtos)
                .map(SucursalDto::of)
                .forEach(sucursalRepository::save);
    }

    @AfterEach
    public void cleanup(){
        // Cleanup the database after each test
        sucursalRepository.deleteAll();
    }

    @Test
    @DisplayName("Test sucursal not found with non-existing id")
    public void testSucursalNotFoundForNonExistingId(){
        // Given two sucursales in the database

        // When
        Optional<Sucursal> retrievedSucursal = sucursalRepository.findById(100l);

        // Then
        Assertions.assertFalse(retrievedSucursal.isPresent(), "Sucursal with id 100 should not exist");
    }


    @Test
    @DisplayName("Test sucursal saved successfully")
    public void testSucursalSavedSuccessfully(){
        // Prepare mock sucursal
        Sucursal newSucursal = new Sucursal("Mendoza Capital", new Longitud(3), new Latitud(3));

        // When
         sucursalRepository.save(newSucursal);

        Optional<Sucursal> savedSucursal = sucursalRepository.findByDireccion(newSucursal.getDireccion());

        // Then
        assertNotNull(savedSucursal.get(), "Sucursal should be saved");
        assertNotNull(savedSucursal.get().getId(), "Sucursal should have an id when saved");

        assertThat(savedSucursal.get()).isEqualToIgnoringGivenFields(newSucursal, "id" );
    }


    @Test
    @DisplayName("Test sucursal updated successfully")
    public void testSucursalUpdatedSuccessfully(){
        // Prepare the sucursal
        Sucursal sucursalToUpdate = new Sucursal(1l, "Nueva Direccion", new Longitud(4), new Latitud(4));

        // When
        sucursalRepository.save(sucursalToUpdate);

        Optional<Sucursal> updatedSucursal = sucursalRepository.findById(sucursalToUpdate.getId());

        // Then
        assertThat(updatedSucursal.get()).isEqualToComparingFieldByField(sucursalToUpdate);
    }

    @Test
    @DisplayName("Test sucursal mas cercana successfully")
    public void testSucursalMasCercanaSuccessfully() throws Exception{

        //Sucursal newSucursal = new Sucursal("Santa Fe Capital", new Longitud(4), new Latitud(4));
        Sucursal newSucursal = new Sucursal("Santa Fe Capital", new Longitud(2.1), new Latitud(4.1));
        sucursalRepository.save(newSucursal);

        Longitud userLongitud = new Longitud(2);
        Latitud userLatitud = new Latitud(4);
        // la sucursal mas cercana a 2,4 es Santa Fe Capital
        Optional<Sucursal> santaFeCapitalSucursal = sucursalRepository.findByDireccion("Santa Fe Capital");

        // When

        Optional<Sucursal> sucursalMasCercana = sucursalRepository.findSucursalMasCercana((Point) new WKTReader().read("POINT(" + userLongitud.getLongitud() + " " + userLatitud.getLatitud() + ")"));

        // Then
        assertNotNull(sucursalMasCercana.get(), "Sucursal mas cercana debe existir");

        //Sucursal mas cercana es Santa Fe Capital
        assertThat(sucursalMasCercana.get().getId()).isEqualTo(santaFeCapitalSucursal.get().getId());
    }

    @Test
    @DisplayName("Test sucursal deleted successfully")
    public void testSucursalDeletedSuccessfully(){
        // Given two sucursales in the database

        // When
        sucursalRepository.deleteById(1l);

        // Then
        Assertions.assertEquals(1L, sucursalRepository.count());
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
