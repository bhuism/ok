package nl.appsource.ok;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class HttpRequestTest {

    @Value(value = "${local.server.port}")
    private int serverPort;

    //    @Value(value = "${management.server.port}")
    private final int managementPort = 9080;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void greetingShouldReturnDefaultMessage() {
        assertThat(this.restTemplate.getForObject("http://localhost:" + serverPort + "/",
            String.class)).isEqualTo("OK");
    }

    @Test
    public void actuatorHealthShouldReturnDefaultMessage() {
        assertThat(this.restTemplate.getForObject("http://localhost:" + managementPort + "/actuator/health",
            String.class)).isEqualTo("{\"status\":\"UP\",\"groups\":[\"liveness\",\"readiness\"]}");
    }

}