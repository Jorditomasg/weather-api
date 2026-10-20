package es.asv.weather.municipality.infrastructure;

import es.asv.weather.municipality.application.SearchMunicipalities;
import es.asv.weather.municipality.domain.MunicipalityRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
class MunicipalityConfig {

    @Bean
    SearchMunicipalities searchMunicipalities(MunicipalityRepository municipalityRepository,
                                              @Value("${municipality.cache.refresh-after}") Duration refreshAfter,
                                              @Value("${municipality.cache.warm-up}") boolean warmUp) {
        CachingMunicipalityRepository cachingRepository = new CachingMunicipalityRepository(municipalityRepository, refreshAfter);
        if (warmUp) {
            cachingRepository.warmUp();
        }
        return new SearchMunicipalities(cachingRepository);
    }
}
