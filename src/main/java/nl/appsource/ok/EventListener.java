package nl.appsource.ok;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.info.GitProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventListener implements ApplicationListener<ApplicationReadyEvent> {

    private final GitProperties gitProperties;

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        log.info(event.getClass().getSimpleName() + ": " + gitProperties.getShortCommitId());
    }

}