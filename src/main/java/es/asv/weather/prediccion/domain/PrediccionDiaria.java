package es.asv.weather.prediccion.domain;

import java.time.LocalDate;
import java.util.List;

/**
 * Predicción de un municipio para un día. Las temperaturas se guardan en grados Celsius,
 * que es como las publica AEMET; la conversión a otras unidades se hace al consultarlas.
 */
public record PrediccionDiaria(
        LocalDate fecha,
        int temperaturaMaximaCelsius,
        int temperaturaMinimaCelsius,
        List<ProbabilidadPrecipitacion> probPrecipitacion) {

    public PrediccionDiaria {
        probPrecipitacion = List.copyOf(probPrecipitacion);
    }

    // AEMET no publica la media diaria: se usa la media de extremos, el criterio meteorológico habitual
    public double mediaTemperatura(UnidadTemperatura unidad) {
        double mediaCelsius = (temperaturaMaximaCelsius + temperaturaMinimaCelsius) / 2.0;
        return Math.round(unidad.desdeCelsius(mediaCelsius) * 10) / 10.0;
    }
}
