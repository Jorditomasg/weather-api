# weather-api

REST API to get tomorrow's weather forecast for any municipality in Spain, backed by [AEMET OpenData](https://opendata.aemet.es/).

## Requirements

- Java 25
- An AEMET API key ([request it here](https://opendata.aemet.es/centrodedescargas/altaUsuario), it arrives by email)

Maven is not required: the project ships with the `./mvnw` wrapper.

## Running locally

```bash
cp .env.example .env          # then paste your API key into AEMET_API_KEY
./mvnw spring-boot:run
```

- API: http://localhost:8080/api
- Swagger UI: http://localhost:8080/swagger-ui.html

The key can also be passed as an environment variable: `AEMET_API_KEY=... ./mvnw spring-boot:run`.

## Tests

```bash
./mvnw test
```

Tests never call AEMET: its responses are simulated, so no API key or network is needed. They run on every push and pull request through GitHub Actions.

## Endpoints

The JSON field names and the `G_CEL` / `G_FAH` codes follow the contract given in the assignment.

### Municipality search

`GET /api/municipalities?name=Ag`

```json
[
  { "codigo": "03002", "nombre": "Agost" },
  { "codigo": "03003", "nombre": "Agres" }
]
```

Prefix search, case and accent insensitive, sorted alphabetically.

### Tomorrow's forecast

`GET /api/municipalities/{code}/forecast?unit=G_FAH`

`unit` is optional (`G_CEL` by default, or `G_FAH`).

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

Errors ([RFC 9457](https://www.rfc-editor.org/rfc/rfc9457) format): `400` invalid parameters, `404` no forecast for tomorrow, `502` AEMET unavailable.

## Architecture

Hexagonal architecture, organised by feature:

```
es.asv.weather
├── municipality/
│   ├── domain/           Municipality, MunicipalityRepository (port)
│   ├── application/      SearchMunicipalities (use case)
│   └── infrastructure/   AEMET adapter, REST controller, wiring
├── forecast/
│   ├── domain/           DailyForecast, TemperatureUnit, ForecastRepository (port)
│   ├── application/      GetTomorrowForecast (use case)
│   └── infrastructure/   AEMET adapter, REST controller, wiring
└── shared/infrastructure/
    ├── aemet/            AemetClient: AEMET two-step call and ISO-8859-15 decoding
    └── web/              global error handling
```

- **Domain** and **use cases** depend neither on Spring nor on AEMET.
- AEMET details (`id` prefix in codes, date format, 24/12/6 hour periods, Spanish field names) are translated inside the adapters and never leak out.
- `shared` does not depend on any feature.

### Decisions

- **Average temperature**: AEMET publishes max and min, not the mean. We use `(max + min) / 2`, rounded to one decimal.
- **Tomorrow**: computed in `Europe/Madrid` time (configurable), not the server's.
- **Precipitation probability**: 6 hour periods are returned, as in the assignment example.
- **Municipality cache**: the list barely changes and AEMET is slow, so it is kept in memory (Caffeine), preloaded at startup and refreshed in the background every 24 h (`municipality.cache.refresh-after`). If AEMET fails, the previous list keeps being served. It is a decorator of the repository port, so domain and use case are untouched.
