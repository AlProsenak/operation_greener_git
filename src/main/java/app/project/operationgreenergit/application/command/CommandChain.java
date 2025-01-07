package app.project.operationgreenergit.application.command;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CommandChain {

	private final Command command;
	private final Operator operator;

	public static CommandChain from(Command command, Operator operator) {
		return new CommandChain(command, operator);
	}

	public String getOperator() {
		return this.operator.value;
	}

	@RequiredArgsConstructor
	public enum Operator {

		AND(" && "),
		END("; "),
		OR(" || "),
		PIPE(" | ");

		private final String value;

	}

}
