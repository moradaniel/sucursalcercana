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

#### Crear sucursal ejemplo endpoint

```
    curl --location --request POST 'http://localhost:8080/sucursales/sucursales' \
    --header 'Content-Type: application/json' \
    --data-raw '{
    "direccion": "Capital Federal",
    "longitud":"2",
    "latitud":"2"
    }'

```

#### Obtener sucursal por id ejemplo endpoint

```
    curl --location --request GET 'http://localhost:8080/sucursales/sucursales/1' \
```

#### Obtener sucursal mas cercana a una coordenada geografica

```
    curl --location --request GET 'http://localhost:8080/sucursales/sucursales/sucursalmascercana?latitud=4&longitud=2'

```
