package app.project.operationgreenergit.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;

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

	// Message template
	private static final String EXCEPTION_CAUGHT = "Exception caught: %s";
	private static final String PROCESS_START_FAILED = "Failed to start process: %s";
	private static final String PROCESS_INTERRUPTED = "Process interrupted: %s";
	private static final String PROCESS_CODE_EXIT = "Process: %s exit code: %s";
	private static final String PROCESS_CODE_ERROR_EXIT = "Process: %s exit code: %s with error: %s";

	public void generateCommitHistory() {
		validateSupportedOS();
		validateSystemGitVersion();

		// Initialize cache directory.
		executeProcess(CACHE_DIR_GEN_PB);
		// Clone project repository into cache.
		cloneRepository();

		// Switch to clean Git branch.
		executeProcess(GIT_MAIN_BRANCH_SWITCH_PB);
		executeProcess(GIT_WORK_BRANCH_DELETE_PB);
		executeProcess(GIT_WORK_BRANCH_CREATE_PB);
		executeProcess(GIT_WORK_BRANCH_SWITCH_PB);

		// Create and write to dummy file.
		createFile();

		// Create dummy commits.
		executeProcess(GIT_ADD_ALL_PB);
		executeProcess(GIT_COMMIT_README_PB);

		// Push to remote branch.
		executeProcess(GIT_PUSH_ORIGIN_WORK_PB);
	}

	private static void validateSupportedOS() {
		if (isSupportedOS()) {
			return;
		}
		// TODO: Move this logic to initialization phase.
		throw new RuntimeException("Service does not support Windows operating system");
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
			log.debug("System Git version: " + gitVersion);

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
					throw new RuntimeException("Repository: %s not found".formatted(REPO_URL));
				}
				if (error.contains("already exists")) {
					log.debug("Repository: %s is already cloned".formatted(REPO_URL));
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
			throw new RuntimeException(reason, ex);
		}
	}

	private static void executeProcess(ProcessBuilder processBuilder) {
		try {
			Process process = processBuilder.start();
			int exitCode = process.waitFor();

			if (exitCode == 0) {
				log.debug(PROCESS_CODE_EXIT.formatted(processBuilder.command().toString(), exitCode));
				return;
			}

			String error = readInputStream(process.getErrorStream());
			log.debug(PROCESS_CODE_ERROR_EXIT.formatted(processBuilder.command().toString(), exitCode, error));
		} catch (IOException ex) {
			String reason = PROCESS_START_FAILED.formatted(CACHE_DIR_GEN_PB.command().toString());
			log.error(EXCEPTION_CAUGHT.formatted(reason), ex);
			throw new RuntimeException(reason, ex);
		} catch (InterruptedException ex) {
			String reason = PROCESS_INTERRUPTED.formatted(processBuilder.command().toString());
			log.error(EXCEPTION_CAUGHT.formatted(reason), ex);
			throw new RuntimeException(reason, ex);
		}
	}

	private static String readInputStream(InputStream inputStream) throws IOException {
		if (inputStream == null) {
			return null;
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line).append("\n");
		}
		reader.close();

		return sb.toString();
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
			log.error("Caught exception", ex);
			throw new RuntimeException("Failed to operate on file", ex);
		}
	}

}
