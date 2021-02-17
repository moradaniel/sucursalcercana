# Sucursales web-app

#### Aplicacion que permite obtener la sucursal mas cercana a una coordenada geografica

### To build the project run the following commands:
```
$docker-compose -f ./docker-compose.yml up -d dbpostgis_test
$./mvnw clean install

```

### To build the web app Docker image:

```./mvnw install -DskipTests dockerfile:build```

### To run the web app  Docker image:

```
$docker-compose -f ./docker-compose.yml up -d dbpostgis
$docker-compose -f ./docker-compose.yml up -d gis-sucursales-webapp

```


###Swagger api documentation
http://localhost:8080/sucursales/swagger-ui/index.html
http://localhost:8080/sucursales/v2/api-docs


###Health check endpoints
http://localhost:8080/sucursales/actuator/health