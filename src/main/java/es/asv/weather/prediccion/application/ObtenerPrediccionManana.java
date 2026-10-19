package es.asv.weather.prediccion.application;

import es.asv.weather.prediccion.domain.PrediccionDiaria;
import es.asv.weather.prediccion.domain.PrediccionNoEncontradaException;
import es.asv.weather.prediccion.domain.PrediccionRepository;

import java.time.Clock;
import java.time.LocalDate;

public class ObtenerPrediccionManana {

    private final PrediccionRepository prediccionRepository;
    private final Clock clock;

    // El Clock se inyecta para que "mañana" sea testeable y dependa de la zona horaria configurada
    public ObtenerPrediccionManana(PrediccionRepository prediccionRepository, Clock clock) {
        this.prediccionRepository = prediccionRepository;
        this.clock = clock;
    }

    public PrediccionDiaria obtener(String codigoMunicipio) {
        LocalDate manana = LocalDate.now(clock).plusDays(1);
        return prediccionRepository.findByMunicipioAndFecha(codigoMunicipio, manana)
                .orElseThrow(() -> new PrediccionNoEncontradaException(codigoMunicipio, manana));
    }
}
