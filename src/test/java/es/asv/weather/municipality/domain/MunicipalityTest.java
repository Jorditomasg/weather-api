package es.asv.weather.municipality.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MunicipalityTest {

    private final Municipality agost = new Municipality("03002", "Agost");

    @Test
    void nameStartsWithIsCaseInsensitive() {
        assertThat(agost.nameStartsWith("ag")).isTrue();
        assertThat(agost.nameStartsWith("AG")).isTrue();
    }

    @Test
    void nameStartsWithIgnoresAccents() {
        Municipality avila = new Municipality("05019", "Ávila");

        assertThat(avila.nameStartsWith("avi")).isTrue();
        assertThat(agost.nameStartsWith("Ág")).isTrue();
    }

    @Test
    void doesNotMatchWhenPrefixIsNotAtTheStart() {
        assertThat(agost.nameStartsWith("gost")).isFalse();
    }

    @Test
    void rejectsBlankCodeOrName() {
        assertThatThrownBy(() -> new Municipality(" ", "Agost")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Municipality("03002", null)).isInstanceOf(IllegalArgumentException.class);
    }
}
