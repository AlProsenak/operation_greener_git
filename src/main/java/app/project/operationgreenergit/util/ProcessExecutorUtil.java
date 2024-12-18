package app.project.operationgreenergit.util;

import app.project.operationgreenergit.exception.ExceptionSupplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.io.IOException;

import static app.project.operationgreenergit.util.InputStreamUtil.readInputStream;
import static app.project.operationgreenergit.util.MessageTemplate.EXCEPTION_CAUGHT;
import static app.project.operationgreenergit.util.MessageTemplate.EXCEPTION_UTILITY_CLASS;
import static app.project.operationgreenergit.util.MessageTemplate.MUST_NOT_BE_NULL;
import static app.project.operationgreenergit.util.MessageTemplate.PROCESS_CODE_ERROR_EXIT;
import static app.project.operationgreenergit.util.MessageTemplate.PROCESS_CODE_EXIT;
import static app.project.operationgreenergit.util.MessageTemplate.PROCESS_INTERRUPTED;
import static app.project.operationgreenergit.util.MessageTemplate.PROCESS_START_FAILED;

@Slf4j
public final class ProcessExecutorUtil {

	private ProcessExecutorUtil() {
		throw new UnsupportedOperationException(EXCEPTION_UTILITY_CLASS.formatted(ProcessExecutorUtil.class.getName()));
	}

	public static void executeProcess(ProcessBuilder processBuilder) throws IOException, InterruptedException {
		Assert.notNull(processBuilder, MUST_NOT_BE_NULL.formatted("processBuilder"));

		Process process = processBuilder.start();
		int exitCode = process.waitFor();

		if (exitCode == 0) {
			log.debug(PROCESS_CODE_EXIT.formatted(processBuilder.command().toString(), exitCode));
			return;
		}

		String error = readInputStream(process.getErrorStream());
		log.debug(PROCESS_CODE_ERROR_EXIT.formatted(processBuilder.command().toString(), exitCode, error));
	}

	public static void executeHandledProcess(
			ProcessBuilder processBuilder,
			ExceptionSupplier<?> exceptionSupplier) {
		Assert.notNull(exceptionSupplier, MUST_NOT_BE_NULL.formatted("exceptionSupplier"));

		try {
			executeProcess(processBuilder);
		} catch (IOException ex) {
			String reason = PROCESS_START_FAILED.formatted(processBuilder.command().toString());
			log.error(EXCEPTION_CAUGHT.formatted(reason), ex);
			throw exceptionSupplier.get(reason, ex);
		} catch (InterruptedException ex) {
			String reason = PROCESS_INTERRUPTED.formatted(processBuilder.command().toString());
			log.error(EXCEPTION_CAUGHT.formatted(reason), ex);
			throw new RuntimeException(reason, ex);
		}
	}

}
