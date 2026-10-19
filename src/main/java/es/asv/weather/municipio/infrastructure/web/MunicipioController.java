package es.asv.weather.municipio.infrastructure.web;

import es.asv.weather.municipio.application.BuscarMunicipios;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/municipios")
class MunicipioController {

    private final BuscarMunicipios buscarMunicipios;

    MunicipioController(BuscarMunicipios buscarMunicipios) {
        this.buscarMunicipios = buscarMunicipios;
    }

    @Operation(summary = "Busca municipios cuyo nombre empieza por el prefijo indicado")
    @GetMapping
    List<MunicipioResponse> buscar(@Parameter(example = "Ag") @RequestParam @NotBlank String nombre) {
        return buscarMunicipios.buscar(nombre).stream()
                .map(MunicipioResponse::from)
                .toList();
    }
}
