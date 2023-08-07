package nl.appsource.ok;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

@RestController
public class OkController {

    private static final Set<String> AVAILABLE_ZONE_IDS = ZoneId.getAvailableZoneIds();

    private static final Function<Optional<String>, Optional<String>> mytime = (zone) -> Optional.of(ISO_OFFSET_DATE_TIME.format(LocalDateTime.now().atZone(
        zone
            .filter(AVAILABLE_ZONE_IDS::contains)
            .map(ZoneId::of)
            .orElse(ZoneId.systemDefault())
    )));
    public static final String X_ORIGINAL_FORWARDED_FOR = "x-original-forwarded-for";

    @GetMapping("/")
    public ResponseEntity<String> home(
        final HttpServletRequest httpServletRequest,
        @RequestHeader(X_ORIGINAL_FORWARDED_FOR) final Optional<String> originalForwardedFor,
        @RequestParam final Optional<String> zone
    ) {

        final ServerHttpRequest request = new ServletServerHttpRequest(httpServletRequest);
        final UriComponents uriComponents = UriComponentsBuilder.fromHttpRequest(request).build();

        return Optional.ofNullable(uriComponents.getHost()).flatMap(name -> switch (name) {
                case "ok.impl.nl" -> Optional.of("OK");
                case "ip.impl.nl" -> originalForwardedFor;
                case "time.impl.nl" -> mytime.apply(zone);
                default -> Optional.empty();
            })
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

}