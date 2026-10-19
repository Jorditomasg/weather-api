package es.asv.weather.municipio.application;

import es.asv.weather.municipio.domain.Municipio;
import es.asv.weather.municipio.domain.MunicipioRepository;

import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class BuscarMunicipios {

    private static final Comparator<Municipio> POR_NOMBRE =
            Comparator.comparing(Municipio::nombre, Collator.getInstance(Locale.of("es", "ES")));

    private final MunicipioRepository municipioRepository;

    public BuscarMunicipios(MunicipioRepository municipioRepository) {
        this.municipioRepository = municipioRepository;
    }

    public List<Municipio> buscar(String prefijo) {
        if (prefijo == null || prefijo.isBlank()) throw new IllegalArgumentException("prefijo obligatorio");

        return municipioRepository.findAll().stream()
                .filter(municipio -> municipio.nombreEmpiezaPor(prefijo.strip()))
                .sorted(POR_NOMBRE)
                .toList();
    }
}
