package es.asv.weather.prediccion.domain;

import java.time.LocalDate;
import java.util.Optional;

public interface PrediccionRepository {

    Optional<PrediccionDiaria> findByMunicipioAndFecha(String codigoMunicipio, LocalDate fecha);
}
