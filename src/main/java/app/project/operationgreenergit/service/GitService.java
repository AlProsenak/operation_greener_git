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

	private static final ProcessBuilder GIT_VERSION_PB = new ProcessBuilder("git", "--version");

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

			StringBuilder gitVersion = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				gitVersion.append(line).append("\n");
			}
			log.debug(gitVersion.toString());
		} catch (IOException ex) {
			log.error("Caught exception", ex);
			throw new RuntimeException("Git is not installed");
		}
	}

}
