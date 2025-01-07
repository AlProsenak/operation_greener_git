package app.project.operationgreenergit.application.command.git;

import app.project.operationgreenergit.application.command.Command;
import app.project.operationgreenergit.application.command.CommandLine;
import app.project.operationgreenergit.application.command.option.Option;

import java.util.List;

public class GitCommand extends Command {

	public GitCommand(CommandLine commandLine, String command, String argument, List<Option> options) {
		super(commandLine.getCommandChain(), commandLine.getProgram(), command, argument, options);
	}

	// TODO: options
	public GitCommand force() {
		this.options.add(Option.from("-f", ""));
		return this;
	}

}
