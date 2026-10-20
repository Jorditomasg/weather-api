package es.asv.weather.shared.infrastructure.web;

import es.asv.weather.shared.infrastructure.aemet.AemetException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

@RestControllerAdvice
class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    // AEMET's limit is per minute ("Vuelva a intentarlo el próximo minuto")
    private static final String RETRY_AFTER_SECONDS = "60";

    // Not an outage: AEMET answered but our per-minute quota is spent, so tell the client when to come back
    @ExceptionHandler(HttpClientErrorException.TooManyRequests.class)
    ResponseEntity<ProblemDetail> aemetRateLimited(HttpClientErrorException.TooManyRequests e) {
        log.warn("AEMET rate limit reached: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .header(HttpHeaders.RETRY_AFTER, RETRY_AFTER_SECONDS)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE,
                        "AEMET usage limit reached, try again in a minute"));
    }

    // AEMET down or invalid key: the upstream provider failed, not the client
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
