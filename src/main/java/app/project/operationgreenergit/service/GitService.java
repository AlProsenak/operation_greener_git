package app.project.operationgreenergit.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
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

	// Process builder
	private static final ProcessBuilder GIT_VERSION_PB = new ProcessBuilder("git", "--version");
	private static final ProcessBuilder CACHE_DIR_GEN_PB = new ProcessBuilder("mkdir", "-p", CACHE_DIR);
	private static final ProcessBuilder GIT_CLONE_PB = new ProcessBuilder("git", "clone", REPO_URL)
			.directory(Paths.get(CACHE_DIR).toFile());

	private static final ProcessBuilder GIT_MAIN_BRANCH_SWITCH_PB = new ProcessBuilder("git", "switch", MAIN_BRANCH_NAME)
			.directory(Paths.get(REPO_DIR).toFile());
	private static final ProcessBuilder GIT_WORK_BRANCH_CREATE_PB = new ProcessBuilder("git", "branch", WORK_BRANCH_NAME)
			.directory(Paths.get(REPO_DIR).toFile());
	private static final ProcessBuilder GIT_WORK_BRANCH_SWITCH_PB = new ProcessBuilder("git", "switch", WORK_BRANCH_NAME)
			.directory(Paths.get(REPO_DIR).toFile());
	private static final ProcessBuilder GIT_WORK_BRANCH_DELETE_PB = new ProcessBuilder("git", "branch", "-D", WORK_BRANCH_NAME)
			.directory(Paths.get(REPO_DIR).toFile());

	public void generateCommitHistory() {
		// Validate operating system.
		String os = System.getProperty("os.name");
		boolean isWindows = os.equalsIgnoreCase("windows");
		if (isWindows) {
			throw new RuntimeException("Service does not support Windows operating system");
		}

		// Validate Git is installed.
		try {
			Process process = GIT_VERSION_PB.start();

			InputStream inputStream = process.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
			reader.close();

			// Trim last part of Git version output.
			// Output example: 'git version 2.45.1'.
			String gitMessage = sb.toString().trim();
			String[] gitParts = gitMessage.split("\\s+");
			String gitVersion = gitParts[gitParts.length - 1];
			log.debug("Installed Git version: " + gitVersion);

			int exitCode = process.waitFor();
			log.debug("Exited with code: " + exitCode + " process: " + GIT_VERSION_PB.command().toString());
		} catch (IOException ex) {
			// Command 'git' is not found.
			// Exception output example: 'Cannot run program "git": error=2, No such file or directory'.
			log.error("Caught exception", ex);
			throw new RuntimeException("Program: '" + GIT_VERSION_PB.command().getFirst() + "' not found", ex);
		} catch (InterruptedException ex) {
			throw new RuntimeException("Process was interrupted", ex);
		}

		// Initialize cache repository directory.
		try {
			Process process = CACHE_DIR_GEN_PB.start();

			int exitCode = process.waitFor();
			log.debug("Exited with code: " + exitCode + " process: " + CACHE_DIR_GEN_PB.command().toString());
		} catch (IOException ex) {
			log.error("Caught exception", ex);
			throw new RuntimeException("Could not initialize cache directory");
		} catch (InterruptedException ex) {
			throw new RuntimeException("Process was interrupted", ex);
		}

		// Clone project repository into cache.
		try {
			Process process = GIT_CLONE_PB.start();

			int exitCode = process.waitFor();
			log.debug("Exited with code: " + exitCode + " process: " + GIT_CLONE_PB.command().toString());

			if (exitCode != 0) {
				InputStream errorInputStream = process.getErrorStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(errorInputStream));
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					sb.append(line).append("\n");
				}
				reader.close();

				// Handle cloning repository errors.
				// Error output example 1: 'ERROR: Repository not found.\nfatal: Could not read from remote repository.'
				// Error output example 2: 'fatal: destination path 'REPOSITORY_NAME' already exists and is not an empty directory.'
				String error = sb.toString();
				log.error("Caught process: " + GIT_CLONE_PB.command().toString() + " error: '" + error + "'");
				if (error.contains("Repository not found")) {
					throw new RuntimeException("Repository: '" + REPO_URL + "' not found");
				}
				if (error.contains("already exists")) {
					log.debug("Repository: '" + REPO_URL + "' is already cloned");
				}
			}
		} catch (IOException ex) {
			// Command 'git' is not found.
			// Exception output example: 'Cannot run program "git": error=2, No such file or directory'.
			log.error("Caught exception", ex);
			throw new RuntimeException("Program: '" + GIT_CLONE_PB.command().getFirst() + "' not found", ex);
		} catch (InterruptedException ex) {
			throw new RuntimeException("Process was interrupted", ex);
		}

		// Switch to clean Git branch.
		try {
			Process process1 = GIT_MAIN_BRANCH_SWITCH_PB.start();
			int exitCode1 = process1.waitFor();
			log.debug("Exited with code: " + exitCode1 + " process: " + GIT_MAIN_BRANCH_SWITCH_PB.command().toString());

			Process process2 = GIT_WORK_BRANCH_DELETE_PB.start();
			int exitCode2 = process2.waitFor();
			log.debug("Exited with code: " + exitCode2 + " process: " + GIT_WORK_BRANCH_DELETE_PB.command().toString());

			Process process3 = GIT_WORK_BRANCH_CREATE_PB.start();
			int exitCode3 = process3.waitFor();
			log.debug("Exited with code: " + exitCode3 + " process: " + GIT_WORK_BRANCH_CREATE_PB.command().toString());

			Process process4 = GIT_WORK_BRANCH_SWITCH_PB.start();
			int exitCode4 = process4.waitFor();
			log.debug("Exited with code: " + exitCode4 + " process: " + GIT_WORK_BRANCH_SWITCH_PB.command().toString());
		} catch (IOException ex) {
			log.error("Caught exception", ex);
			throw new RuntimeException("Program: '" + GIT_MAIN_BRANCH_SWITCH_PB.command().getFirst() + "' not found", ex);
		} catch (InterruptedException ex) {
			throw new RuntimeException("Process was interrupted", ex);
		}
	}

}
