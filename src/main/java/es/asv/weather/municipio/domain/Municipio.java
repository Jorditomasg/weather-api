package es.asv.weather.municipio.domain;

import java.text.Normalizer;
import java.util.Locale;

public record Municipio(String codigo, String nombre) {

    public Municipio {
        if (codigo == null || codigo.isBlank()) throw new IllegalArgumentException("codigo obligatorio");
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("nombre obligatorio");
    }

    public boolean nombreEmpiezaPor(String prefijo) {
        return normalizar(nombre).startsWith(normalizar(prefijo));
    }

    private static String normalizar(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
    }
}
