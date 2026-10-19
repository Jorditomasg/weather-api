package es.asv.weather.municipality.application;

import es.asv.weather.municipality.domain.Municipality;
import es.asv.weather.municipality.domain.MunicipalityRepository;

import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class SearchMunicipalities {

    private static final Comparator<Municipality> BY_NAME =
            Comparator.comparing(Municipality::name, Collator.getInstance(Locale.of("es", "ES")));

    private final MunicipalityRepository municipalityRepository;

    public SearchMunicipalities(MunicipalityRepository municipalityRepository) {
        this.municipalityRepository = municipalityRepository;
    }

    public List<Municipality> search(String prefix) {
        if (prefix == null || prefix.isBlank()) throw new IllegalArgumentException("prefix is required");

        return municipalityRepository.findAll().stream()
                .filter(municipality -> municipality.nameStartsWith(prefix.strip()))
                .sorted(BY_NAME)
                .toList();
    }
}
