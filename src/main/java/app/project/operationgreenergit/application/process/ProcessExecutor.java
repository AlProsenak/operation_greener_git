package app.project.operationgreenergit.application.process;

import app.project.operationgreenergit.exception.ExceptionSupplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.List;

import static app.project.operationgreenergit.util.InputStreamUtil.readInputStream;
import static app.project.operationgreenergit.util.MessageTemplate.EXCEPTION_CAUGHT;
import static app.project.operationgreenergit.util.MessageTemplate.MUST_NOT_BE_NULL;
import static app.project.operationgreenergit.util.MessageTemplate.PROCESS_INTERRUPTED;
import static app.project.operationgreenergit.util.MessageTemplate.PROCESS_START_FAILED;

@Slf4j
public class ProcessExecutor {

	private final ProcessBuilder processBuilder;

	private boolean readOutput;
	private ExceptionSupplier<?> ioExceptionSupplier;
	private ExceptionSupplier<?> interruptedExceptionSupplier;

	public ProcessExecutor(ProcessBuilder processBuilder) {
		Assert.notNull(processBuilder, MUST_NOT_BE_NULL.formatted("processBuilder"));

		this.readOutput = false;
		this.ioExceptionSupplier = RuntimeException::new;
		this.interruptedExceptionSupplier = RuntimeException::new;
		this.processBuilder = processBuilder;
	}

	public List<String> command() {
		return this.processBuilder.command();
	}

	public ProcessExecutor command(String... command) {
		this.processBuilder.command(command);
		return this;
	}

	public ProcessExecutor command(List<String> command) {
		this.processBuilder.command(command);
		return this;
	}

	public ProcessExecutor setIOExceptionSupplier(ExceptionSupplier<?> ioExceptionSupplier) {
		Assert.notNull(ioExceptionSupplier, MUST_NOT_BE_NULL.formatted("ioExceptionSupplier"));

		this.ioExceptionSupplier = ioExceptionSupplier;
		return this;
	}

	public ProcessExecutor setInterruptedExceptionSupplier(ExceptionSupplier<?> interruptedExceptionSupplier) {
		Assert.notNull(interruptedExceptionSupplier, MUST_NOT_BE_NULL.formatted("interruptedExceptionSupplier"));

		this.interruptedExceptionSupplier = interruptedExceptionSupplier;
		return this;
	}

	public ProcessExecutor readOutput() {
		this.readOutput = true;
		return this;
	}

	public ProcessExecutor ignoreOutput() {
		this.readOutput = false;
		return this;
	}

	public ProcessResult start() throws IOException, InterruptedException {
		Process process = this.processBuilder.start();
		int exitCode = process.waitFor();
		String[] command = this.processBuilder.command().toArray(String[]::new);

		String output = null;
		if (readOutput) {
			output = readInputStream(process.getInputStream());
		}

		String error = null;
		if (exitCode != 0) {
			error = readInputStream(process.getErrorStream());
		}

		return new ProcessResult(command, exitCode, output, error);
	}

	public ProcessResult startHandled() {
		try {
			return start();
		} catch (IOException ex) {
			String reason = PROCESS_START_FAILED.formatted(processBuilder.command().toString());
			log.error(EXCEPTION_CAUGHT.formatted(reason), ex);
			throw this.ioExceptionSupplier.get(reason, ex);
		} catch (InterruptedException ex) {
			String reason = PROCESS_INTERRUPTED.formatted(processBuilder.command().toString());
			log.error(EXCEPTION_CAUGHT.formatted(reason), ex);
			Thread.currentThread().interrupt();
			throw this.interruptedExceptionSupplier.get(reason, ex);
		}
	}

}
