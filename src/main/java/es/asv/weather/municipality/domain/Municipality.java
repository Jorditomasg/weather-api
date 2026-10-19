package es.asv.weather.municipality.domain;

import java.text.Normalizer;
import java.util.Locale;

public record Municipality(String code, String name) {

    public Municipality {
        if (code == null || code.isBlank()) throw new IllegalArgumentException("code is required");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name is required");
    }

    public boolean nameStartsWith(String prefix) {
        return normalize(name).startsWith(normalize(prefix));
    }

    private static String normalize(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
    }
}
