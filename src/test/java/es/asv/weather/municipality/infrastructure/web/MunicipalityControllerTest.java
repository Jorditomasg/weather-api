package es.asv.weather.municipality.infrastructure.web;

import es.asv.weather.municipality.application.SearchMunicipalities;
import es.asv.weather.municipality.domain.Municipality;
import es.asv.weather.shared.infrastructure.aemet.AemetException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@WebMvcTest(MunicipalityController.class)
class MunicipalityControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private SearchMunicipalities searchMunicipalities;

    @Test
    void returnsMunicipalitiesWithContractFieldNames() {
        given(searchMunicipalities.search("Ag")).willReturn(List.of(new Municipality("03002", "Agost")));

        assertThat(mvc.get().uri("/api/municipalities?name=Ag"))
                .hasStatusOk()
                .bodyJson().isEqualTo("""
                        [{"codigo": "03002", "nombre": "Agost"}]
                        """);
    }

    @Test
    void returnsBadRequestWithBlankName() {
        assertThat(mvc.get().uri("/api/municipalities?name=")).hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void returnsBadGatewayWhenAemetFails() {
        given(searchMunicipalities.search("Ag")).willThrow(new AemetException("down"));

        assertThat(mvc.get().uri("/api/municipalities?name=Ag")).hasStatus(HttpStatus.BAD_GATEWAY);
    }
}
