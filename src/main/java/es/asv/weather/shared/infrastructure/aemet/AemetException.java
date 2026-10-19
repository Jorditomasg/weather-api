package es.asv.weather.shared.infrastructure.aemet;

public class AemetException extends RuntimeException {

    public AemetException(String message) {
        super(message);
    }
}
