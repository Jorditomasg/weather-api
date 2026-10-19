package es.asv.weather.municipality.application;

import es.asv.weather.municipality.domain.Municipality;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SearchMunicipalitiesTest {

    private final Municipality agres = new Municipality("03003", "Agres");
    private final Municipality abades = new Municipality("40001", "Abades");
    private final Municipality agost = new Municipality("03002", "Agost");

    private final SearchMunicipalities searchMunicipalities = new SearchMunicipalities(() -> List.of(agres, abades, agost));

    @Test
    void returnsOnlyMunicipalitiesStartingWithPrefix() {
        assertThat(searchMunicipalities.search("Ag")).containsExactlyInAnyOrder(agres, agost);
    }

    @Test
    void returnsResultsSortedByName() {
        assertThat(searchMunicipalities.search("A")).containsExactly(abades, agost, agres);
    }

    @Test
    void returnsEmptyListWhenNothingMatches() {
        assertThat(searchMunicipalities.search("Zz")).isEmpty();
    }

    @Test
    void rejectsBlankPrefix() {
        assertThatThrownBy(() -> searchMunicipalities.search(" ")).isInstanceOf(IllegalArgumentException.class);
    }
}
