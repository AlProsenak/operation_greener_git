package app.project.operationgreenergit.application.process;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.File;

import static app.project.operationgreenergit.util.MessageTemplate.MUST_NOT_BE_NULL;
import static app.project.operationgreenergit.util.MessageTemplate.NON_SUPPORTED_OS;
import static app.project.operationgreenergit.util.SystemPropertyUtil.OS_NAME;
import static app.project.operationgreenergit.util.SystemPropertyUtil.isLinux;
import static app.project.operationgreenergit.util.SystemPropertyUtil.isMacOS;

@Component
public class ProcessBuilderManager {

	private final String shell;
	private final String shellArgument;

	private ProcessBuilderManager() {
		if (isLinux() || isMacOS()) {
			this.shell = "/bin/bash";
			this.shellArgument = "-c";
		} else {
			throw new UnsupportedOperationException(NON_SUPPORTED_OS.formatted(OS_NAME));
		}
	}

	public static ProcessBuilderManager getInstance() {
		return new ProcessBuilderManager();
	}

	public ProcessBuilder createProcessBuilder(String command) {
		Assert.notNull(command, MUST_NOT_BE_NULL.formatted("command"));
		// TODO: command validation and sanitization
		return new ProcessBuilder(this.shell, this.shellArgument, command);
	}

	public ProcessBuilder createScriptPB(File path) {
		return new ProcessBuilder(this.shell, "");
	}

}
