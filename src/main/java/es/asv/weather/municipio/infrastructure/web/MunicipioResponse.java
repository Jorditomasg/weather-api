package es.asv.weather.municipio.infrastructure.web;

import es.asv.weather.municipio.domain.Municipio;

record MunicipioResponse(String codigo, String nombre) {

    static MunicipioResponse from(Municipio municipio) {
        return new MunicipioResponse(municipio.codigo(), municipio.nombre());
    }
}
