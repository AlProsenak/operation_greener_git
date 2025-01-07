package app.project.operationgreenergit.application.command;

import app.project.operationgreenergit.application.command.git.GitCommandLine;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class CommandInitializer {

	private final List<CommandChain> chainedCommands;

	private CommandInitializer() {
		this.chainedCommands = new ArrayList<>();
	}

	public static CommandInitializer initialize() {
		return new CommandInitializer();
	}

	public GitCommandLine git() {
		return new GitCommandLine(this.chainedCommands);
	}

	public static void test() {
		var s = CommandInitializer.initialize()
				.git()
				.version()
				.and()
				.git()
				.branch("pepe")
				.swap()
				.getCommandOutput()
		;

		System.out.println(s);
	}

}
