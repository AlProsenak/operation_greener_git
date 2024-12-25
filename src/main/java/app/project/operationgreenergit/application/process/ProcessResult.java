package app.project.operationgreenergit.application.process;

import app.project.operationgreenergit.exception.ExceptionMessageSupplier;
import lombok.Getter;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;

import static app.project.operationgreenergit.util.MessageTemplate.MUST_NOT_BE_NULL;
import static app.project.operationgreenergit.util.MessageTemplate.NOT_FOUND;
import static app.project.operationgreenergit.util.MessageTemplate.PROCESS_CODE_ERROR_EXIT;
import static app.project.operationgreenergit.util.MessageTemplate.PROCESS_CODE_EXIT;


@Getter
public class ProcessResult {

	private final String[] command;
	private final int exitCode;
	private final String standardOutput;
	private final String standardError;

	public ProcessResult(String[] command, int exitCode, String standardOutput, String standardError) {
		Assert.notNull(command, MUST_NOT_BE_NULL.formatted("command"));

		this.command = command;
		this.exitCode = exitCode;

		if (standardOutput == null || standardOutput.isBlank()) {
			this.standardOutput = null;
		} else {
			this.standardOutput = standardOutput;
		}

		if (standardError == null || standardError.isBlank()) {
			this.standardError = null;
		} else {
			this.standardError = standardError;
		}
	}

	public boolean isSuccessfulExit() {
		return exitCode == 0;
	}

	public Optional<String> getStandardOutput() {
		return Optional.ofNullable(this.standardOutput);
	}

	public String getStandardOutputOrThrow(ExceptionMessageSupplier<?> exceptionSupplier) {
		return getStandardOutput().orElseThrow(() ->
				exceptionSupplier.get(NOT_FOUND.formatted("'standardOutput'")));
	}

	public Optional<String> getStandardError() {
		return Optional.ofNullable(this.standardError);
	}

	public String getStandardErrorOrThrow(ExceptionMessageSupplier<?> exceptionSupplier) {
		return getStandardError().orElseThrow(() ->
				exceptionSupplier.get(NOT_FOUND.formatted("'standardError'")));
	}

	public Optional<String> getAnyOutput() {
		return isSuccessfulExit() ? getStandardOutput() : getStandardError();
	}

	public String getAnyOutputOrThrow(ExceptionMessageSupplier<?> exceptionSupplier) {
		return getStandardOutput().orElseGet(() ->
				getStandardError().orElseThrow(() ->
						exceptionSupplier.get(NOT_FOUND.formatted("[standardOutput, standardError]"))));
	}

	public ProcessResult log(Consumer<String> logMethod) {
		if (this.exitCode == 0) {
			logMethod.accept(PROCESS_CODE_EXIT.formatted(Arrays.toString(this.command), this.exitCode));
		} else {
			logMethod.accept(PROCESS_CODE_ERROR_EXIT.formatted(Arrays.toString(this.command), this.exitCode, this.standardError));
		}
		return this;
	}

}
