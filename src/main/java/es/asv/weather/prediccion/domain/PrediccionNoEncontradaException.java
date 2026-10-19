package es.asv.weather.prediccion.domain;

import java.time.LocalDate;

public class PrediccionNoEncontradaException extends RuntimeException {

    public PrediccionNoEncontradaException(String codigoMunicipio, LocalDate fecha) {
        super("No hay predicción para el municipio " + codigoMunicipio + " el " + fecha);
    }
}
