package es.asv.weather.shared.infrastructure.web;

import es.asv.weather.shared.infrastructure.aemet.AemetException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;

@RestControllerAdvice
class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // AEMET caída, clave inválida o límite de peticiones: el fallo es del proveedor, no del cliente
    @ExceptionHandler({AemetException.class, RestClientException.class})
    ProblemDetail aemetNoDisponible(RuntimeException e) {
        log.error("Error consultando AEMET", e);
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY, "El servicio de AEMET no está disponible");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail peticionInvalida(IllegalArgumentException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }
}
