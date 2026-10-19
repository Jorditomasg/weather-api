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

    // AEMET down, invalid key or rate limit: the upstream provider failed, not the client
    @ExceptionHandler({AemetException.class, RestClientException.class})
    ProblemDetail aemetUnavailable(RuntimeException e) {
        log.error("Error calling AEMET", e);
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY, "AEMET service is unavailable");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail badRequest(IllegalArgumentException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }
}
