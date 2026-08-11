package nl.appsource.ok;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalManagementPort;
import org.springframework.web.client.RestClient;

import java.util.Objects;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

@SpringBootTest(webEnvironment = DEFINED_PORT)
public class HttpRequestTest {

    @Value(value = "${local.server.port}")
    private int serverPort;

    @LocalManagementPort
    private int managementPort;

    @Test
    public void greetingShouldReturnOkMessage() {
        assertThat(
            RestClient.create()
                .get()
                .uri("http://localhost:" + serverPort + "/")
                .header("x-forwarded-host", "ok.impl.nl")
                .retrieve()
                .body(String.class)
        ).isEqualTo("ok");
    }

    @Test
    public void greetingShouldReturnTimeMessage() {
        assertThat(
            ISO_OFFSET_DATE_TIME.parse(
                Objects.requireNonNull(RestClient.create()
                    .get()
                    .uri("http://localhost:" + serverPort + "/")
                    .header("x-forwarded-host", "time.impl.nl")
                    .retrieve()
                    .body(String.class))
            )).isNotNull();
    }

    @Test
    public void greetingShouldReturnIp() {
        assertThat(
            RestClient.create()
                .get()
                .uri("http://localhost:" + serverPort + "/")
                .header("x-forwarded-host", "ip.impl.nl")
                .header("x-real-ip", "a.b.c.d")
                .retrieve()
                .body(String.class)
        ).isEqualTo("a.b.c.d");
    }

    @Test
    public void actuatorHealthShouldReturnDefaultMessage() {
        assertThat(RestClient.create()
            .get()
            .uri("http://localhost:" + managementPort + "/manage/health")
            .retrieve()
            .body(String.class)).isEqualTo("{\"groups\":[\"liveness\",\"readiness\"],\"status\":\"UP\"}");
    }

    @Test
    public void actuatorHealthLiveNessShouldReturnDefaultMessage() {
        assertThat(RestClient.create()
            .get()
            .uri("http://localhost:" + managementPort + "/manage/health/liveness")
            .retrieve()
            .body(String.class)).isEqualTo("{\"status\":\"UP\"}");
    }

    @Test
    public void actuatorHealthReadinessShouldReturnDefaultMessage() {
        assertThat(RestClient.create()
            .get()
            .uri("http://localhost:" + managementPort + "/manage/health/readiness")
            .retrieve()
            .body(String.class)).isEqualTo("{\"status\":\"UP\"}");
    }

}