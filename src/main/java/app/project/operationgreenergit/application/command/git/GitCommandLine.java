package app.project.operationgreenergit.application.command.git;

import app.project.operationgreenergit.application.command.CommandChain;
import app.project.operationgreenergit.application.command.CommandLine;

import java.util.ArrayList;
import java.util.List;

public class GitCommandLine extends CommandLine {

	private static final String COMMAND = "git";

	public GitCommandLine(List<CommandChain> chainedCommands) {
		super(chainedCommands, COMMAND);
	}

	public GitBranch branch(String branchName) {
		return new GitBranch(this, branchName);
	}

	public GitCommand version() {
		return new GitCommand(this, "--version", "", new ArrayList<>());
	}

}
