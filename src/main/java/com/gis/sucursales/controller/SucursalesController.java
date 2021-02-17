package com.gis.sucursales.controller;

import com.gis.sucursales.dto.ApiResponse;
import com.gis.sucursales.dto.SucursalCreateDtoValidator;
import com.gis.sucursales.dto.SucursalDto;
import com.gis.sucursales.service.SucursalService;
import com.gis.sucursales.model.Latitud;
import com.gis.sucursales.model.Longitud;
import com.gis.sucursales.model.Sucursal;
import io.swagger.annotations.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Api(tags = {"SucursalesController"})
@SwaggerDefinition(tags = {
        @Tag(name = "Swagger SucursalesController", description = "REST API related to Sucursales")
})
@RestController
@RequestMapping(value = "/sucursales")
public class SucursalesController {

    private static final Logger LOGGER = LogManager.getLogger(SucursalesController.class);

    @Autowired
    private SucursalService sucursalService;

    @Autowired
    private SucursalCreateDtoValidator sucursalCreateDtoValidator;


    /**
     * Get the Sucursal with specified ID
     * @param   id ID of the Sucursal to get
     * @return  ResponseEntity with the found Sucursal
     *          or NOT_FOUND if no Sucursal found
     */
    @GetMapping("/{id}")
    @ApiOperation("Returns a specific sucursal by their identifier. 404 if does not exist.")

    public ResponseEntity<ApiResponse<SucursalDto>> getSucursal(
            @ApiParam("Id of the sucursal to be obtained. Cannot be empty.")
            @PathVariable Long id){


        Optional<Sucursal> sucursalOptional = sucursalService.findById(id);
        if (sucursalOptional.isPresent()){

            ApiResponse<SucursalDto> apiResponse = new ApiResponse(SucursalDto.of(sucursalOptional.get()));

                return ResponseEntity
                        .ok()
                        .body(apiResponse);
        } else {
            return ResponseEntity.notFound().build();
        }
    }



    /**
     * Get the Sucursal with specified ID
     * @param   longitud longitud de la coordenada
     * @param   latitud latitud de la coordenada
     * @return  ResponseEntity with the found Sucursal
     *          or NOT_FOUND if no Sucursal found
     */
    @GetMapping("/sucursalmascercana")
    @ApiOperation("Sucursal mas cercana a una coordenada geografica. 404 if does not exist.")

    public ResponseEntity<ApiResponse<SucursalDto>> getSucursalMasCercana(
            @ApiParam("longitud. Cannot be empty.")
            @RequestParam Long longitud,
            @ApiParam("latitud. Cannot be empty.")
            @RequestParam Long latitud) throws Exception{


        Optional<Sucursal> sucursalOptional = sucursalService.findSucursalMasCercana(new Longitud(longitud),new Latitud(latitud));
        if (sucursalOptional.isPresent()){
           ApiResponse<SucursalDto> apiResponse = new ApiResponse(SucursalDto.of(sucursalOptional.get()));
            return ResponseEntity
                    .ok()
                    .body(apiResponse);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    /**
     * Saves a new Sucursal
     * @param   sucursalCreateDTO Sucursal to save
     * @return  ResponseEntity with the saved Sucursal
     */
    @PostMapping("")
    public ResponseEntity<ApiResponse<SucursalDto>> saveSucursal(@Valid @RequestBody SucursalDto sucursalCreateDTO){
        LOGGER.info("Adding new sucursal with direccion:{}", sucursalCreateDTO.getDireccion());

        Errors bindingResult = new BeanPropertyBindingResult(sucursalCreateDTO, sucursalCreateDTO.getClass().getName());

        sucursalCreateDtoValidator.validate(sucursalCreateDTO,bindingResult);
        if (bindingResult.hasErrors()) {
            List<String> errors = new ArrayList<>();
            for (ObjectError error : bindingResult.getAllErrors()) {
                errors.add(error.getCode()+"-"+error.getDefaultMessage());
            }

            ApiResponse<SucursalDto> apiResponse = new ApiResponse(errors);
            return ResponseEntity
                    .badRequest()
                    .body(apiResponse);

        }else {

            Sucursal newSucursal = sucursalService.save(SucursalDto.of(sucursalCreateDTO));
            try {
                ApiResponse<SucursalDto> apiResponse = new ApiResponse(SucursalDto.of(newSucursal));
                return ResponseEntity
                        .created(new URI("/sucursales/" + newSucursal.getId()))
                        .body(apiResponse);
            } catch (URISyntaxException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
    }


}
