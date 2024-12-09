package app.project.operationgreenergit.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Slf4j
@Service
public class GitService {

	private static final String USER_HOME = System.getProperty("user.home");
	private static final String CACHE_DIR = USER_HOME + "/.cache";

	// Process builders
	private static final ProcessBuilder GIT_VERSION_PB = new ProcessBuilder("git", "--version");
	private static final ProcessBuilder CACHE_DIR_GEN_PB = new ProcessBuilder("mkdir", "-p", CACHE_DIR);

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
			throw new RuntimeException("Git is not installed", ex);
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
	}

}
