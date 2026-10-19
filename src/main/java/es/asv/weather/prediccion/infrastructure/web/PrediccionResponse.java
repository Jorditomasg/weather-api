package es.asv.weather.prediccion.infrastructure.web;

import es.asv.weather.prediccion.domain.PrediccionDiaria;
import es.asv.weather.prediccion.domain.ProbabilidadPrecipitacion;
import es.asv.weather.prediccion.domain.UnidadTemperatura;

import java.util.List;

record PrediccionResponse(
        double mediaTemperatura,
        UnidadTemperatura unidadTemperatura,
        List<ProbabilidadPrecipitacion> probPrecipitacion) {

    static PrediccionResponse from(PrediccionDiaria prediccion, UnidadTemperatura unidad) {
        return new PrediccionResponse(prediccion.mediaTemperatura(unidad), unidad, prediccion.probPrecipitacion());
    }
}
