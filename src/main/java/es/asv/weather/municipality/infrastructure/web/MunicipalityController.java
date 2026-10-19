package es.asv.weather.municipality.infrastructure.web;

import es.asv.weather.municipality.application.SearchMunicipalities;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/municipalities")
class MunicipalityController {

    private final SearchMunicipalities searchMunicipalities;

    MunicipalityController(SearchMunicipalities searchMunicipalities) {
        this.searchMunicipalities = searchMunicipalities;
    }

    @Operation(summary = "Search municipalities whose name starts with the given prefix")
    @GetMapping
    List<MunicipalityResponse> search(@Parameter(example = "Ag") @RequestParam @NotBlank String name) {
        return searchMunicipalities.search(name).stream()
                .map(MunicipalityResponse::from)
                .toList();
    }
}
