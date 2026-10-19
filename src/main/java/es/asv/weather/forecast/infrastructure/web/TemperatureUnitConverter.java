package es.asv.weather.forecast.infrastructure.web;

import es.asv.weather.forecast.domain.TemperatureUnit;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

// Lets request params use the contract codes (G_CEL / G_FAH) instead of the enum names
@Component
class TemperatureUnitConverter implements Converter<String, TemperatureUnit> {

    @Override
    public TemperatureUnit convert(String code) {
        return TemperatureUnit.fromCode(code);
    }
}
