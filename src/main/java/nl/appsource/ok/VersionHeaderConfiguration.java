package nl.appsource.ok;


import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.info.GitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class VersionHeaderConfiguration {

    public static final String VERSION = "Version";

    private final GitProperties gitProperties;

    @Bean
    public Filter addVersionHeader() {
        return (request, servletResponse, filterChain) -> {
            final HttpServletResponse response = (HttpServletResponse) servletResponse;
            response.addHeader(VERSION, gitProperties.getShortCommitId());
            filterChain.doFilter(request, response);
        };
    }

}
