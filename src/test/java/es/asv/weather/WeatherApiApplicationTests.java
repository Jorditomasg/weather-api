package es.asv.weather;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "municipality.cache.warm-up=false") // tests never call AEMET
class WeatherApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
