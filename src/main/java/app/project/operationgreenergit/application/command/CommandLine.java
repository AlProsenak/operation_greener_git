package app.project.operationgreenergit.application.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public abstract class CommandLine {

	protected final List<CommandChain> commandChain;
	protected final String program;

}
