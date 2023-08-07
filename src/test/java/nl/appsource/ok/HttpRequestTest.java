package nl.appsource.ok;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.util.Objects;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;
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
    public void greetingShouldReturnOkMessage() {
        final HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set("x-forwarded-host", "ok.impl.nl");
        final HttpEntity<String> entity = new HttpEntity<>(requestHeaders);
        assertThat(
            this.restTemplate.exchange("http://localhost:" + serverPort + "/", HttpMethod.GET,
                entity,
                String.class
            ).getBody()
        ).isEqualTo("OK");
    }

    @Test
    public void greetingShouldReturnTimeMessage() {
        final HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set("x-forwarded-host", "time.impl.nl");
        final HttpEntity<String> entity = new HttpEntity<>(requestHeaders);
        assertThat(
            ISO_OFFSET_DATE_TIME.parse(
                Objects.requireNonNull(this.restTemplate.exchange("http://localhost:" + serverPort + "/", HttpMethod.GET,
                    entity,
                    String.class
                ).getBody())
            )).isNotNull();
    }

    @Test
    public void greetingShouldReturnIp() {
        final HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set("x-forwarded-host", "ip.impl.nl");
        requestHeaders.set("x-original-forwarded-for", "a.b.c.d");
        final HttpEntity<String> entity = new HttpEntity<>(requestHeaders);
        assertThat(
            this.restTemplate.exchange("http://localhost:" + serverPort + "/", HttpMethod.GET,
                entity,
                String.class
            ).getBody()
        ).isEqualTo("a.b.c.d");
    }

    @Test
    public void actuatorHealthShouldReturnDefaultMessage() {
        assertThat(this.restTemplate.getForObject("http://localhost:" + managementPort + "/actuator/health",
            String.class)).isEqualTo("{\"status\":\"UP\",\"groups\":[\"liveness\",\"readiness\"]}");
    }

}