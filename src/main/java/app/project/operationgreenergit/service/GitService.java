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

	private static final String REPO_NAME = "operation_greener_git";
	private static final String REPO_URL = "https://github.com/AlProsenak/" + REPO_NAME + ".git";

	private static final String USER_HOME = System.getProperty("user.home");
	private static final String CACHE_DIR = USER_HOME + "/.cache";

	// Process builders
	private static final ProcessBuilder GIT_VERSION_PB = new ProcessBuilder("git", "--version");
	private static final ProcessBuilder CACHE_DIR_GEN_PB = new ProcessBuilder("mkdir", "-p", CACHE_DIR);
	private static final ProcessBuilder GIT_CLONE_PB = new ProcessBuilder("git", "clone", REPO_URL)
			.directory(Paths.get(CACHE_DIR).toFile());

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

			String gitMessage = sb.toString().trim();
			String[] gitParts = gitMessage.split("\\s+");
			String gitVersion = gitParts[gitParts.length - 1];
			log.debug("Installed Git version: " + gitVersion);

			int exitCode = process.waitFor();
			log.debug("Exited with code: " + exitCode + " process: " + GIT_VERSION_PB.command().toString());
		} catch (IOException ex) {
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

			if (exitCode == 0) {
				return;
			}

			InputStream errorInputStream = process.getErrorStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(errorInputStream));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
			reader.close();

			String error = sb.toString();
			log.error("Caught process: " + GIT_CLONE_PB.command().toString() + " error: '" + error + "'");
			if (error.contains("Repository not found")) {
				throw new RuntimeException("Repository: '" + REPO_URL + "' not found");
			}
			if (error.contains("already exists")) {
				log.debug("Repository: '" + REPO_URL + "' is already cloned");
			}
		} catch (IOException ex) {
			log.error("Caught exception", ex);
			throw new RuntimeException("Program: '" + GIT_CLONE_PB.command().getFirst() + "' not found", ex);
		} catch (InterruptedException ex) {
			throw new RuntimeException("Process was interrupted", ex);
		}
	}

}
