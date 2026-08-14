package dev.meher.portfolio.gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class FallbackControllerTest {

    @Test
    void returnsAnExplicitServiceUnavailableResponse() {
        ResponseEntity<Map<String, Object>> response = new FallbackController().fallback("inventory");

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals("inventory", response.getBody().get("service"));
        assertEquals("temporarily-unavailable", response.getBody().get("status"));
    }
}
