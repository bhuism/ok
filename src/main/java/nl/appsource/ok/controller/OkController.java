package nl.appsource.ok.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ForwardedHeaderUtils;
import org.springframework.web.util.UriComponents;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

@Slf4j
@RestController
public class OkController {

    private static final Set<String> AVAILABLE_ZONE_IDS = ZoneId.getAvailableZoneIds();

    private static final Function<Optional<String>, Optional<String>> MYYIME = (zone) -> Optional.of(ISO_OFFSET_DATE_TIME.format(LocalDateTime.now().atZone(
        zone
            .filter(AVAILABLE_ZONE_IDS::contains)
            .map(ZoneId::of)
            .orElse(ZoneId.systemDefault())
    )));
    public static final String X_REAL_IP = "x-real-ip";
    private static final Consumer<HttpHeaders> DEFAULT_HEADERS = httpHeaders -> {
        httpHeaders.add("Access-Control-Allow-Origin", "*");
        httpHeaders.add("Access-Control-Allow-Headers", "*");
        httpHeaders.add("Access-Control-Allow-Methods", "*");
    };

    @GetMapping("/**")
    @SuppressWarnings("DesignForExtension")
    public ResponseEntity<String> home(
        final HttpServletRequest httpServletRequest,
        @RequestHeader(X_REAL_IP) final Optional<String> originalForwardedFor,
        @RequestParam final Optional<String> zone
    ) {

        final ServerHttpRequest request = new ServletServerHttpRequest(httpServletRequest);
        final UriComponents uriComponents = ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()).build();

        final String remoteAddr = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getRemoteAddr();

        log.info("{} {}", remoteAddr, uriComponents.toUri());

//        request.getHeaders().forEach((s, strings) -> log.info(" Got header: " + s + "=" + strings));

        return Optional.ofNullable(uriComponents.getHost())
            .flatMap(name -> switch (name) {
                case "ok-ok-service.okapp":
                case "ok.impl.nl":
                case "localhost": {
                    yield Optional.of("ok");
                }
                case "ok-ip-service.okapp":
                case "ip.impl.nl": {
                    yield originalForwardedFor;
                }
                case "ok-time-service.okapp":
                case "time.impl.nl": {
                    yield MYYIME.apply(zone);
                }
                default: {
                    log.warn("Unknown host: " + name);
                    yield Optional.empty();
                }
            })
            .map(string -> ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).headers(DEFAULT_HEADERS).body(string))
            .orElse(ResponseEntity.notFound().headers(DEFAULT_HEADERS).build());
    }

}
