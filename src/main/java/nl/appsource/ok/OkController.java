package nl.appsource.ok;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Slf4j
@RestController
public class OkController {

    @GetMapping("/")
    public ResponseEntity<String> home(
        final HttpServletRequest httpServletRequest,
        @RequestHeader("x-original-forwarded-for") final Optional<String> originalForwardedFor
    ) {

        final ServerHttpRequest request = new ServletServerHttpRequest(httpServletRequest);
        final UriComponents uriComponents = UriComponentsBuilder.fromHttpRequest(request).build();

        return Optional.ofNullable(uriComponents.getHost()).flatMap(name -> switch (name) {
                case "ok.impl.nl" -> Optional.of("OK");
                case "ip.impl.nl" -> originalForwardedFor;
                default -> Optional.empty();
            })
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

}