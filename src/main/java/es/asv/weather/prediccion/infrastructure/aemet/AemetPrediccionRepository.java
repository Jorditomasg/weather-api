package es.asv.weather.prediccion.infrastructure.aemet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import es.asv.weather.prediccion.domain.PrediccionDiaria;
import es.asv.weather.prediccion.domain.PrediccionRepository;
import es.asv.weather.prediccion.domain.ProbabilidadPrecipitacion;
import es.asv.weather.shared.infrastructure.aemet.AemetClient;
import org.springframework.stereotype.Repository;
import tools.jackson.core.type.TypeReference;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Repository
public class AemetPrediccionRepository implements PrediccionRepository {

    private static final TypeReference<List<AemetPrediccionMunicipio>> PREDICCIONES = new TypeReference<>() {
    };
    // AEMET mezcla periodos de 24, 12 y 6 horas ("00-24", "00-12", "00-06"...); nos quedamos con los de 6
    private static final Pattern PERIODO = Pattern.compile("(\\d{2})-(\\d{2})");

    private final AemetClient aemetClient;

    public AemetPrediccionRepository(AemetClient aemetClient) {
        this.aemetClient = aemetClient;
    }

    @Override
    public Optional<PrediccionDiaria> findByMunicipioAndFecha(String codigoMunicipio, LocalDate fecha) {
        return aemetClient.get("/api/prediccion/especifica/municipio/diaria/" + codigoMunicipio, PREDICCIONES).stream()
                .flatMap(prediccion -> prediccion.prediccion().dia().stream())
                .filter(dia -> dia.fechaLocal().equals(fecha))
                .findFirst()
                .map(AemetDia::toDomain);
    }

    private static boolean esPeriodoDeSeisHoras(String periodo) {
        Matcher matcher = PERIODO.matcher(periodo == null ? "" : periodo);
        return matcher.matches() && Integer.parseInt(matcher.group(2)) - Integer.parseInt(matcher.group(1)) == 6;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record AemetPrediccionMunicipio(AemetPrediccion prediccion) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record AemetPrediccion(List<AemetDia> dia) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record AemetDia(String fecha, AemetTemperatura temperatura, List<AemetProbPrecipitacion> probPrecipitacion) {

        // AEMET envía la fecha como "2026-10-20T00:00:00"
        LocalDate fechaLocal() {
            return LocalDate.parse(fecha.substring(0, 10));
        }

        PrediccionDiaria toDomain() {
            List<ProbabilidadPrecipitacion> probabilidades = probPrecipitacion.stream()
                    .filter(prob -> prob.value() != null && esPeriodoDeSeisHoras(prob.periodo()))
                    .map(prob -> new ProbabilidadPrecipitacion(prob.value(), prob.periodo()))
                    .toList();
            return new PrediccionDiaria(fechaLocal(), temperatura.maxima(), temperatura.minima(), probabilidades);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record AemetTemperatura(int maxima, int minima) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record AemetProbPrecipitacion(Integer value, String periodo) {
    }
}
