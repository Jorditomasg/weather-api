package es.asv.weather.municipio.infrastructure.aemet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import es.asv.weather.municipio.domain.Municipio;
import es.asv.weather.municipio.domain.MunicipioRepository;
import es.asv.weather.shared.infrastructure.aemet.AemetClient;
import org.springframework.stereotype.Repository;
import tools.jackson.core.type.TypeReference;

import java.util.List;

@Repository
public class AemetMunicipioRepository implements MunicipioRepository {

    private static final TypeReference<List<AemetMunicipio>> MUNICIPIOS = new TypeReference<>() {
    };

    private final AemetClient aemetClient;

    public AemetMunicipioRepository(AemetClient aemetClient) {
        this.aemetClient = aemetClient;
    }

    @Override
    public List<Municipio> findAll() {
        return aemetClient.get("/api/maestro/municipios", MUNICIPIOS).stream()
                .map(AemetMunicipio::toDomain)
                .toList();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record AemetMunicipio(String id, String nombre) {

        // AEMET identifica con "id" + código INE ("id03002"); nuestro código es solo el INE
        Municipio toDomain() {
            return new Municipio(id.replaceFirst("^id", ""), nombre);
        }
    }
}
