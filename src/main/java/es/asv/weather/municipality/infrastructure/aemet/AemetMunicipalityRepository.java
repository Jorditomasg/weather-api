package es.asv.weather.municipality.infrastructure.aemet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import es.asv.weather.municipality.domain.Municipality;
import es.asv.weather.municipality.domain.MunicipalityRepository;
import es.asv.weather.shared.infrastructure.aemet.AemetClient;
import org.springframework.stereotype.Repository;
import tools.jackson.core.type.TypeReference;

import java.util.List;

@Repository
public class AemetMunicipalityRepository implements MunicipalityRepository {

    private static final TypeReference<List<AemetMunicipality>> MUNICIPALITIES = new TypeReference<>() {
    };

    private final AemetClient aemetClient;

    public AemetMunicipalityRepository(AemetClient aemetClient) {
        this.aemetClient = aemetClient;
    }

    @Override
    public List<Municipality> findAll() {
        return aemetClient.get("/api/maestro/municipios", MUNICIPALITIES).stream()
                .map(AemetMunicipality::toDomain)
                .toList();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record AemetMunicipality(String id, @JsonProperty("nombre") String name) {

        // AEMET ids are "id" + INE code ("id03002"); our code is the bare INE code
        Municipality toDomain() {
            return new Municipality(id.replaceFirst("^id", ""), name);
        }
    }
}
