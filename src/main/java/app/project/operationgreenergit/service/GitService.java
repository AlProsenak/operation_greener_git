package app.project.operationgreenergit.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

import static app.project.operationgreenergit.util.InputStreamUtil.readInputStream;
import static app.project.operationgreenergit.util.MessageTemplate.EXCEPTION_CAUGHT;
import static app.project.operationgreenergit.util.MessageTemplate.FILE_OPERATION_FAILED;
import static app.project.operationgreenergit.util.MessageTemplate.GIT_VERSION;
import static app.project.operationgreenergit.util.MessageTemplate.NON_SUPPORTED_OS;
import static app.project.operationgreenergit.util.MessageTemplate.PROCESS_CODE_ERROR_EXIT;
import static app.project.operationgreenergit.util.MessageTemplate.PROCESS_CODE_EXIT;
import static app.project.operationgreenergit.util.MessageTemplate.PROCESS_INTERRUPTED;
import static app.project.operationgreenergit.util.MessageTemplate.PROCESS_START_FAILED;
import static app.project.operationgreenergit.util.MessageTemplate.REPOSITORY_ALREADY_CLONED;
import static app.project.operationgreenergit.util.MessageTemplate.REPOSITORY_NOT_FOUND;
import static app.project.operationgreenergit.util.ProcessExecutorUtil.executeHandledProcess;

@Slf4j
@Service
public class GitService {

	// Repository
	private static final String REPO_NAME = "operation_greener_git";
	private static final String REPO_URL = "https://github.com/AlProsenak/" + REPO_NAME + ".git";

	// Branch
	private static final String MAIN_BRANCH_NAME = "main";
	private static final String WORK_BRANCH_NAME = "greener_git";

	// Directory
	private static final String USER_HOME = System.getProperty("user.home");
	private static final String CACHE_DIR = USER_HOME + "/.cache";
	private static final String REPO_DIR = CACHE_DIR + "/" + REPO_NAME;

	// File
	private static final File WORK_FILE = new File(REPO_DIR + "/GREENER_GIT.md");

	// Process builder
	private static final ProcessBuilder GIT_VERSION_PB = new ProcessBuilder("git", "--version");
	private static final ProcessBuilder CACHE_DIR_GEN_PB = new ProcessBuilder("mkdir", "-p", CACHE_DIR);
	private static final ProcessBuilder GIT_CLONE_PB = new ProcessBuilder("git", "clone", REPO_URL)
			.directory(Paths.get(CACHE_DIR).toFile());
	private static final ProcessBuilder GIT_MAIN_BRANCH_SWITCH_PB = new ProcessBuilder("git", "switch", "-f", MAIN_BRANCH_NAME)
			.directory(Paths.get(REPO_DIR).toFile());
	private static final ProcessBuilder GIT_WORK_BRANCH_CREATE_PB = new ProcessBuilder("git", "branch", WORK_BRANCH_NAME)
			.directory(Paths.get(REPO_DIR).toFile());
	private static final ProcessBuilder GIT_WORK_BRANCH_SWITCH_PB = new ProcessBuilder("git", "switch", WORK_BRANCH_NAME)
			.directory(Paths.get(REPO_DIR).toFile());
	private static final ProcessBuilder GIT_WORK_BRANCH_DELETE_PB = new ProcessBuilder("git", "branch", "-D", WORK_BRANCH_NAME)
			.directory(Paths.get(REPO_DIR).toFile());
	private static final ProcessBuilder GIT_ADD_ALL_PB = new ProcessBuilder("git", "add", "-A")
			.directory(Paths.get(REPO_DIR).toFile());
	private static final ProcessBuilder GIT_COMMIT_README_PB = new ProcessBuilder("git", "commit", "-m", "Update README.md")
			.directory(Paths.get(REPO_DIR).toFile());
	private static final ProcessBuilder GIT_PUSH_ORIGIN_WORK_PB = new ProcessBuilder("git", "push", "-f", "origin", WORK_BRANCH_NAME)
			.directory(Paths.get(REPO_DIR).toFile());

	public void generateCommitHistory() {
		validateSupportedOS();
		validateSystemGitVersion();

		// Initialize cache directory.
		executeHandledProcess(CACHE_DIR_GEN_PB, RuntimeException::new);
		// Clone project repository into cache.
		cloneRepository();

		// Switch to clean Git branch.
		executeHandledProcess(GIT_MAIN_BRANCH_SWITCH_PB, RuntimeException::new);
		executeHandledProcess(GIT_WORK_BRANCH_DELETE_PB, RuntimeException::new);
		executeHandledProcess(GIT_WORK_BRANCH_CREATE_PB, RuntimeException::new);
		executeHandledProcess(GIT_WORK_BRANCH_SWITCH_PB, RuntimeException::new);

		// Create and write to dummy file.
		createFile();

		// Create dummy commits.
		executeHandledProcess(GIT_ADD_ALL_PB, RuntimeException::new);
		executeHandledProcess(GIT_COMMIT_README_PB, RuntimeException::new);

		// Push to remote branch.
		executeHandledProcess(GIT_PUSH_ORIGIN_WORK_PB, RuntimeException::new);
	}

	private static void validateSupportedOS() {
		if (isSupportedOS()) {
			return;
		}
		// TODO: Move this logic to initialization phase.
		throw new RuntimeException(NON_SUPPORTED_OS.formatted("'Windows'"));
	}

	private static boolean isSupportedOS() {
		String os = System.getProperty("os.name");
		boolean isWindows = os.equalsIgnoreCase("windows");
		return !isWindows;
	}

	private static void validateSystemGitVersion() {
		try {
			Process process = GIT_VERSION_PB.start();

			// Trim last part of Git version output.
			// Output example: 'git version 2.45.1'.
			String gitMessage = readInputStream(process.getInputStream()).trim();
			String[] gitParts = gitMessage.split("\\s+");
			String gitVersion = gitParts[gitParts.length - 1];
			log.debug(GIT_VERSION.formatted(gitVersion));

			int exitCode = process.waitFor();

			log.debug(PROCESS_CODE_EXIT.formatted(GIT_VERSION_PB.command().toString(), exitCode));
		} catch (IOException ex) {
			// Command 'git' is not found.
			// Exception output example: 'Cannot run program "git": error=2, No such file or directory'.
			String reason = PROCESS_START_FAILED.formatted(GIT_VERSION_PB.command().toString());
			log.error(EXCEPTION_CAUGHT.formatted(reason), ex);
			throw new RuntimeException(reason, ex);
		} catch (InterruptedException ex) {
			String reason = PROCESS_INTERRUPTED.formatted(GIT_VERSION_PB.command().toString());
			log.error(EXCEPTION_CAUGHT.formatted(reason), ex);
			Thread.currentThread().interrupt();
			throw new RuntimeException(reason, ex);
		}
	}

	private static void cloneRepository() {
		try {
			Process process = GIT_CLONE_PB.start();
			int exitCode = process.waitFor();

			if (exitCode == 0) {
				log.debug(PROCESS_CODE_EXIT.formatted(GIT_CLONE_PB, exitCode));
			} else {
				// Handle cloning repository errors.
				// Error output example 1: 'ERROR: Repository not found.\nfatal: Could not read from remote repository.'
				// Error output example 2: 'fatal: destination path 'REPOSITORY_NAME' already exists and is not an empty directory.'
				String error = readInputStream(process.getInputStream());
				log.debug(PROCESS_CODE_ERROR_EXIT.formatted(GIT_CLONE_PB.command().toString(), exitCode, error));

				if (error.contains("Repository not found")) {
					throw new RuntimeException(REPOSITORY_NOT_FOUND.formatted(REPO_URL));
				}
				if (error.contains("already exists")) {
					log.debug(REPOSITORY_ALREADY_CLONED.formatted(REPO_URL));
				}
			}
		} catch (IOException ex) {
			// Command 'git' is not found.
			// Exception output example: 'Cannot run program "git": error=2, No such file or directory'.
			String reason = PROCESS_START_FAILED.formatted(GIT_CLONE_PB.command().toString());
			log.error(EXCEPTION_CAUGHT.formatted(reason), ex);
			throw new RuntimeException(reason, ex);
		} catch (InterruptedException ex) {
			String reason = PROCESS_INTERRUPTED.formatted(GIT_CLONE_PB.command().toString());
			log.error(EXCEPTION_CAUGHT.formatted(reason), ex);
			Thread.currentThread().interrupt();
			throw new RuntimeException(reason, ex);
		}
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

}
