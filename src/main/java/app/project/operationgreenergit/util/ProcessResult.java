package app.project.operationgreenergit.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;


@Getter
@RequiredArgsConstructor
public class ProcessResult {

	private final String[] command;
	private final int exitCode;
	private final String output;
	private final String error;

	public boolean isSuccessfulExit() {
		return exitCode == 0;
	}

	public Optional<String> getOutput() {
		return Optional.ofNullable(this.output);
	}

	public Optional<String> getError() {
		return Optional.ofNullable(this.error);
	}

	public Optional<String> getAnyOutput() {
		if (isSuccessfulExit()) {
			return getOutput();
		}
		return getError();
	}

}
