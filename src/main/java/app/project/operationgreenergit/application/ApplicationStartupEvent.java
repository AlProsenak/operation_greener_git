package app.project.operationgreenergit.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static app.project.operationgreenergit.util.MessageTemplate.NON_SUPPORTED_OS;
import static app.project.operationgreenergit.util.SystemPropertyUtil.OS_NAME;
import static app.project.operationgreenergit.util.SystemPropertyUtil.isSupportedOS;

@Slf4j
@Component
public class ApplicationStartupEvent {

	@EventListener(ApplicationReadyEvent.class)
    public void validateSupportedOS() {
		if (isSupportedOS()) {
			return;
		}
		String reason = NON_SUPPORTED_OS.formatted(OS_NAME);
		log.error("Service startup failed: %s".formatted(reason));
		throw new RuntimeException(reason);
	}

}
