package es.asv.weather.prediccion.infrastructure.web;

import es.asv.weather.prediccion.application.ObtenerPrediccionManana;
import es.asv.weather.prediccion.domain.PrediccionNoEncontradaException;
import es.asv.weather.prediccion.domain.UnidadTemperatura;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
class PrediccionController {

    private final ObtenerPrediccionManana obtenerPrediccionManana;

    PrediccionController(ObtenerPrediccionManana obtenerPrediccionManana) {
        this.obtenerPrediccionManana = obtenerPrediccionManana;
    }

    @Operation(summary = "Predicción del día siguiente para un municipio")
    @GetMapping("/api/municipios/{codigo}/prediccion")
    PrediccionResponse obtener(
            @Parameter(example = "03002") @PathVariable @Pattern(regexp = "\\d{5}") String codigo,
            @RequestParam(defaultValue = "G_CEL") UnidadTemperatura unidad) {
        return PrediccionResponse.from(obtenerPrediccionManana.obtener(codigo), unidad);
    }

    @ExceptionHandler(PrediccionNoEncontradaException.class)
    ProblemDetail prediccionNoEncontrada(PrediccionNoEncontradaException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }
}
