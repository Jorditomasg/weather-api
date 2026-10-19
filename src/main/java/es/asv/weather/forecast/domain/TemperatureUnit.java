package es.asv.weather.forecast.domain;

import java.util.Arrays;

public enum TemperatureUnit {

    CELSIUS("G_CEL") {
        @Override
        public double fromCelsius(double celsius) {
            return celsius;
        }
    },
    FAHRENHEIT("G_FAH") {
        @Override
        public double fromCelsius(double celsius) {
            return celsius * 9 / 5 + 32;
        }
    };

    // Code used by the public API contract
    private final String code;

    TemperatureUnit(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }

    public abstract double fromCelsius(double celsius);

    public static TemperatureUnit fromCode(String code) {
        return Arrays.stream(values())
                .filter(unit -> unit.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown temperature unit: " + code));
    }
}
