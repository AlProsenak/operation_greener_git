package app.project.operationgreenergit.service;

import app.project.operationgreenergit.application.process.ProcessBuilderManager;
import app.project.operationgreenergit.application.process.ProcessExecutor;
import app.project.operationgreenergit.application.process.ProcessResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.function.Function;

import static app.project.operationgreenergit.util.CommandTemplate.GIT_ADD_ALL;
import static app.project.operationgreenergit.util.CommandTemplate.GIT_BRANCH_SWITCH;
import static app.project.operationgreenergit.util.CommandTemplate.GIT_BRANCH_SWITCH_TO_RECREATED;
import static app.project.operationgreenergit.util.CommandTemplate.GIT_CLEAN;
import static app.project.operationgreenergit.util.CommandTemplate.GIT_CLONE;
import static app.project.operationgreenergit.util.CommandTemplate.GIT_COMMIT;
import static app.project.operationgreenergit.util.CommandTemplate.GIT_FETCH_PULL;
import static app.project.operationgreenergit.util.CommandTemplate.GIT_PUSH;
import static app.project.operationgreenergit.util.CommandTemplate.GIT_RESET;
import static app.project.operationgreenergit.util.CommandTemplate.GIT_VERSION;
import static app.project.operationgreenergit.util.CommandTemplate.MAKE_DIR;
import static app.project.operationgreenergit.util.MessageTemplate.EXCEPTION_CAUGHT;
import static app.project.operationgreenergit.util.MessageTemplate.FILE_OPERATION_FAILED;
import static app.project.operationgreenergit.util.MessageTemplate.GIT_SYSTEM_VERSION;
import static app.project.operationgreenergit.util.MessageTemplate.PROCESS_CODE_EXIT;
import static app.project.operationgreenergit.util.MessageTemplate.REPOSITORY_ALREADY_CLONED;
import static app.project.operationgreenergit.util.MessageTemplate.REPOSITORY_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitService {

	private final ProcessBuilderManager processBuilderManager;

	// Repository
	private static final String REPO_NAME = "operation_greener_git";
	private static final String REPO_URL = "https://github.com/AlProsenak/" + REPO_NAME + ".git";
	private static final String ORIGIN = "origin";

	// Branch
	private static final String MAIN_BRANCH_NAME = "main";
	private static final String WORK_BRANCH_NAME = "greener_git";

	// Directory
	private static final String USER_HOME = System.getProperty("user.home");
	private static final String CACHE_DIR = USER_HOME + "/.cache";
	private static final String REPO_DIR = CACHE_DIR + "/" + REPO_NAME;

	// File
	private static final File WORK_FILE = new File(REPO_DIR + "/GREENER_GIT.md");

	private static final String GIT_COMMIT_MESSAGE = "Update README.md";

	// Process builder
	// Example when command 'git' is not found.
	// Exception output: 'Cannot run program "git": error=2, No such file or directory'.
	private static final Function<ProcessBuilder, ProcessResult> IGNORE_OUTPUT_PB = pb -> ProcessExecutor.getInstance(pb)
			.ignoreOutput()
			.startHandled()
			.log(log::debug);

	private static final Function<ProcessBuilder, ProcessResult> READ_OUTPUT_PB = pb -> ProcessExecutor.getInstance(pb)
			.readOutput()
			.startHandled()
			.log(log::debug);

	public void generateCommitHistory() {
		validateSystemGitVersion();

		// Initialize cache directory.
		makeDirectory();

		// Clone project repository into cache.
		cloneRepository();

		// Switch to clean Git branch.
		switchToWorkBranch();

		// Create and write to dummy file.
		createFile();

		// Create dummy commits.
		createCommits();

		// Push to remote branch.
		executeCommand(GIT_PUSH.formatted(ORIGIN, WORK_BRANCH_NAME), Paths.get(REPO_DIR).toFile(), IGNORE_OUTPUT_PB);
	}

	private void validateSystemGitVersion() {
		ProcessBuilder gitVersionPb = processBuilderManager
				.createProcessBuilder(GIT_VERSION);

		ProcessResult processResult = ProcessExecutor.getInstance(gitVersionPb)
				.readOutput()
				.startHandled()
				.log(log::debug);

		String gitVersionOutput = processResult.getStandardOutputOrThrow(RuntimeException::new);
		String gitVersion = parseGitVersion(gitVersionOutput);

		log.debug(GIT_SYSTEM_VERSION.formatted(gitVersion));
		log.debug(PROCESS_CODE_EXIT.formatted(gitVersionPb.command().toString(), processResult.getExitCode()));
	}

	private static String parseGitVersion(String gitVersionOutput) {
		// Trim last part of Git version output.
		// Output example: 'git version 2.45.1'.
		String[] gitParts = gitVersionOutput.split("\\s+");
		return gitParts[gitParts.length - 1];
	}

	private void makeDirectory() {
		ProcessBuilder cacheGenPb = processBuilderManager
				.createProcessBuilder(MAKE_DIR.formatted(CACHE_DIR));

		ProcessExecutor.getInstance(cacheGenPb)
				.ignoreOutput()
				.startHandled()
				.log(log::debug);
	}

	private void cloneRepository() {
		ProcessBuilder gitClonePb = processBuilderManager
				.createProcessBuilder(GIT_CLONE.formatted(REPO_URL))
				.directory(Paths.get(CACHE_DIR).toFile());

		ProcessResult gitClonePr = ProcessExecutor.getInstance(gitClonePb)
				.ignoreOutput()
				.startHandled()
				.log(log::debug);

		int exitCode = gitClonePr.getExitCode();
		if (exitCode == 0) {
			log.debug(PROCESS_CODE_EXIT.formatted(gitClonePr.getCommand(), exitCode));
			return;
		}

		// Handle cloning repository errors.
		// Error output example 1: 'ERROR: Repository not found.\nfatal: Could not read from remote repository.'
		// Error output example 2: 'fatal: destination path 'REPOSITORY_NAME' already exists and is not an empty directory.'
		String standardError = gitClonePr.getStandardErrorOrThrow(RuntimeException::new);
		if (standardError.contains("Repository not found")) {
			throw new RuntimeException(REPOSITORY_NOT_FOUND.formatted(REPO_URL));
		}
		if (standardError.contains("already exists")) {
			log.debug(REPOSITORY_ALREADY_CLONED.formatted(REPO_URL));
		}
	}

	private ProcessResult executeCommand(
			String command,
			File directory,
			Function<ProcessBuilder, ProcessResult> processBuilder) {
		ProcessBuilder switchBranchPb = processBuilderManager
				.createProcessBuilder(command)
				.directory(directory);

		return processBuilder.apply(switchBranchPb);
	}

	private void switchToWorkBranch() {
		File repoDirectory = Paths.get(REPO_DIR).toFile();

		executeCommand(GIT_BRANCH_SWITCH.formatted(MAIN_BRANCH_NAME), repoDirectory, IGNORE_OUTPUT_PB);
		executeCommand(GIT_CLEAN, repoDirectory, IGNORE_OUTPUT_PB);

		ProcessResult pullProcessResult = executeCommand(GIT_FETCH_PULL
				.formatted(ORIGIN, MAIN_BRANCH_NAME, ORIGIN, MAIN_BRANCH_NAME), repoDirectory, IGNORE_OUTPUT_PB);

		var pb = processBuilderManager
				.createProcessBuilder("")
				.directory(repoDirectory);

		ProcessExecutor.getInstance(pb)
				.ignoreOutput()
				.startHandled()
				.log(log::debug);

		var test = IGNORE_OUTPUT_PB.apply(processBuilderManager
				.createProcessBuilder("")
				.directory(repoDirectory));

		// TODO: alternative command execution
		//  method for specifying condition for execution

		if (pullProcessResult.getExitCode() != 0
				&& pullProcessResult
				.getStandardErrorOrThrow(RuntimeException::new)
				.contains("You have divergent branches and need to specify how to reconcile them")
		) {
			executeCommand(GIT_RESET.formatted(ORIGIN + "/" + MAIN_BRANCH_NAME), repoDirectory, IGNORE_OUTPUT_PB);
		}

		executeCommand(GIT_BRANCH_SWITCH_TO_RECREATED
				.formatted(WORK_BRANCH_NAME, WORK_BRANCH_NAME, WORK_BRANCH_NAME), repoDirectory, IGNORE_OUTPUT_PB);
	}

	private static void createFile() {
		try {
			boolean isNewFile = false;
			if (!WORK_FILE.exists()) {
				isNewFile = WORK_FILE.createNewFile();
			}

			FileWriter fw = new FileWriter(WORK_FILE, true);
			BufferedWriter bw = new BufferedWriter(fw);
			if (isNewFile) {
				bw.write("Begin operation Greener Git!");
				bw.newLine();
			}
			bw.write("Sir Yes Sir!");
			bw.newLine();
			bw.close();
		} catch (IOException ex) {
			String reason = FILE_OPERATION_FAILED.formatted(WORK_FILE);
			log.error(EXCEPTION_CAUGHT.formatted(reason), ex);
			throw new RuntimeException(reason, ex);
		}
	}

	private void createCommits() {
		File repoDirectory = Paths.get(REPO_DIR).toFile();

		executeCommand(GIT_ADD_ALL, repoDirectory, IGNORE_OUTPUT_PB);
		executeCommand(GIT_COMMIT.formatted(GIT_COMMIT_MESSAGE), repoDirectory, IGNORE_OUTPUT_PB);

		var pb = processBuilderManager.createProcessBuilder("pwd");
		var pr = READ_OUTPUT_PB.apply(pb);

		log.info(pr.getAnyOutputOrThrow(RuntimeException::new));

		var p = Paths.get(pr.getAnyOutputOrThrow(RuntimeException::new));



//		executeCommand()
	}

}
