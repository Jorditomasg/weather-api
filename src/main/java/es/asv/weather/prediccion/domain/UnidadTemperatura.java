package es.asv.weather.prediccion.domain;

public enum UnidadTemperatura {

    G_CEL {
        @Override
        public double desdeCelsius(double celsius) {
            return celsius;
        }
    },
    G_FAH {
        @Override
        public double desdeCelsius(double celsius) {
            return celsius * 9 / 5 + 32;
        }
    };

    public abstract double desdeCelsius(double celsius);
}
