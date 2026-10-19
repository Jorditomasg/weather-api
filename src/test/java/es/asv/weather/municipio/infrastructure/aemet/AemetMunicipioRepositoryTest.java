package es.asv.weather.municipio.infrastructure.aemet;

import es.asv.weather.municipio.domain.Municipio;
import es.asv.weather.shared.infrastructure.aemet.AemetClient;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.Charset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class AemetMunicipioRepositoryTest {

    private static final Charset ISO_8859_15 = Charset.forName("ISO-8859-15");

    private final RestClient.Builder restClientBuilder = RestClient.builder();
    private final MockRestServiceServer aemet = MockRestServiceServer.bindTo(restClientBuilder).build();
    private final AemetMunicipioRepository repository = new AemetMunicipioRepository(
            new AemetClient(restClientBuilder, JsonMapper.builder().build(), "https://aemet.test", "clave"));

    @Test
    void recuperaLosMunicipiosEnDosPasosYLosTraduceAlDominio() {
        aemet.expect(requestTo("https://aemet.test/api/maestro/municipios"))
                .andExpect(header("api_key", "clave"))
                .andRespond(withSuccess("""
                        {"descripcion": "exito", "estado": 200, "datos": "https://aemet.test/sh/datos"}
                        """, MediaType.APPLICATION_JSON));
        aemet.expect(requestTo("https://aemet.test/sh/datos"))
                .andRespond(withSuccess()
                        .contentType(new MediaType("text", "plain", ISO_8859_15))
                        .body("""
                                [{"id": "id26001", "nombre": "Ábalos", "num_hab": "250"}]
                                """.getBytes(ISO_8859_15)));

        assertThat(repository.findAll()).containsExactly(new Municipio("26001", "Ábalos"));
        aemet.verify();
    }
}
