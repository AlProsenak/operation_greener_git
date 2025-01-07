package app.project.operationgreenergit.application.command.git;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class GitBranch {

	private final GitCommandLine commandLine;
	private final String branchName;

	public GitCommand create() {
		return new GitCommand(commandLine, "create", this.branchName, List.of());
	}

	public GitCommand swap() {
		return new GitCommand(commandLine, "swap", this.branchName, List.of());
	}

	public GitCommand delete() {
		return new GitCommand(commandLine, "delete", this.branchName, List.of());
	}

}
