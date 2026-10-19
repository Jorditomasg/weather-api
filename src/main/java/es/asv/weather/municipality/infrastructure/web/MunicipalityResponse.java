package es.asv.weather.municipality.infrastructure.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.asv.weather.municipality.domain.Municipality;

// JSON field names are fixed by the API contract
record MunicipalityResponse(@JsonProperty("codigo") String code, @JsonProperty("nombre") String name) {

    static MunicipalityResponse from(Municipality municipality) {
        return new MunicipalityResponse(municipality.code(), municipality.name());
    }
}
