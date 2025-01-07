package app.project.operationgreenergit.application.command;

import app.project.operationgreenergit.application.command.option.Option;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public abstract class Command {

	private final List<CommandChain> commandChain;

	protected final String program;
	protected final String command;
	protected final String argument;
	protected final List<Option> options;

	// Cached
	private String commandOutput;

	// TODO: fix
	protected Command(CommandLine commandLine) {
		this.commandChain = commandLine.getCommandChain();
		this.program = commandLine.program;
		this.command = "";
		this.argument = "";
		this.options = new ArrayList<>();
	}

	public String getCommandOutput() {
		if (!(this.commandOutput == null || this.commandOutput.isBlank())) {
			return this.commandOutput;
		}

		StringBuilder builder = new StringBuilder();
		for (var cc : this.commandChain) {
			Command c = cc.getCommand();
			builder.append(c.getProgram()).append(" ").append(c.getCommand()).append(" ").append(c.getArgument());
			for (var o : c.getOptions()) {
				builder.append(" ").append(o.getKey()).append(o.getBridge()).append(o.getValue());
			}
			builder.append(cc.getOperator());
		}

		builder.append(this.program).append(" ").append(this.command).append(" ").append(this.argument);
		for (var o : this.options) {
			builder.append(" ").append(o.getKey()).append(o.getBridge()).append(o.getValue());
		}
		this.commandOutput = builder.toString();

		return this.commandOutput;
	}

	public CommandInitializer and() {
		return addToCommandChain(CommandChain.Operator.AND);
	}

	public CommandInitializer end() {
		return addToCommandChain(CommandChain.Operator.END);
	}

	public CommandInitializer or() {
		return addToCommandChain(CommandChain.Operator.OR);
	}

	public CommandInitializer pipe() {
		return addToCommandChain(CommandChain.Operator.PIPE);
	}

	private CommandInitializer addToCommandChain(CommandChain.Operator operator) {
		CommandChain previousCommand = CommandChain.from(this, operator);
		this.commandChain.add(previousCommand);
		return new CommandInitializer(this.commandChain);
	}

}
