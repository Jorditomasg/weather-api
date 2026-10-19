# weather-api

API REST para consultar la predicción meteorológica del día siguiente en cualquier municipio de España, usando [AEMET OpenData](https://opendata.aemet.es/).

## Requisitos

- Java 25
- Una API key de AEMET ([solicitarla aquí](https://opendata.aemet.es/centrodedescargas/altaUsuario), llega por email)

No hace falta instalar Maven: el proyecto incluye el wrapper `./mvnw`.

## Ejecución en local

```bash
cp .env.example .env          # y pega tu API key en AEMET_API_KEY
./mvnw spring-boot:run
```

- API: http://localhost:8080/api
- Swagger UI: http://localhost:8080/swagger-ui.html

También puede pasarse la clave como variable de entorno: `AEMET_API_KEY=... ./mvnw spring-boot:run`.

## Tests

```bash
./mvnw test
```

Los tests no llaman a AEMET: simulan sus respuestas, así que no necesitan API key ni red. Se ejecutan en cada push y pull request con GitHub Actions.

## Endpoints

### Búsqueda de municipios

`GET /api/municipios?nombre=Ag`

```json
[
  { "codigo": "03002", "nombre": "Agost" },
  { "codigo": "03003", "nombre": "Agres" }
]
```

La búsqueda es por prefijo, sin distinguir mayúsculas ni tildes, y devuelve los resultados ordenados alfabéticamente.

### Predicción del día siguiente

`GET /api/municipios/{codigo}/prediccion?unidad=G_FAH`

`unidad` es opcional (`G_CEL` por defecto, o `G_FAH`).

```json
{
  "mediaTemperatura": 66.7,
  "unidadTemperatura": "G_FAH",
  "probPrecipitacion": [
    { "probabilidad": 5, "periodo": "00-06" },
    { "probabilidad": 10, "periodo": "06-12" },
    { "probabilidad": 10, "periodo": "12-18" },
    { "probabilidad": 5, "periodo": "18-24" }
  ]
}
```

Errores (formato [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457)): `400` parámetros inválidos, `404` sin predicción para mañana, `502` AEMET no disponible.

## Arquitectura

Arquitectura hexagonal organizada por funcionalidad:

```
es.asv.weather
├── municipio/
│   ├── domain/           Municipio, MunicipioRepository (puerto)
│   ├── application/      BuscarMunicipios (caso de uso)
│   └── infrastructure/   adaptador AEMET, controlador REST, configuración
├── prediccion/
│   ├── domain/           PrediccionDiaria, UnidadTemperatura, PrediccionRepository (puerto)
│   ├── application/      ObtenerPrediccionManana (caso de uso)
│   └── infrastructure/   adaptador AEMET, controlador REST, configuración
└── shared/infrastructure/
    ├── aemet/            AemetClient: doble llamada a AEMET y decodificación ISO-8859-15
    └── web/              gestión global de errores
```

- El **dominio** y los **casos de uso** no dependen de Spring ni de AEMET.
- Los detalles de AEMET (prefijo `id` en los códigos, formato de fechas, periodos de 24/12/6 horas) se traducen en los adaptadores y no salen de ellos.
- `shared` no depende de ninguna funcionalidad.

### Decisiones

- **Temperatura media**: AEMET publica máxima y mínima, no la media. Se usa `(máxima + mínima) / 2`, redondeada a un decimal.
- **Día siguiente**: se calcula en hora de `Europe/Madrid` (configurable), no en la del servidor.
- **Probabilidad de precipitación**: se devuelven los periodos de 6 horas, como en el ejemplo del enunciado.
