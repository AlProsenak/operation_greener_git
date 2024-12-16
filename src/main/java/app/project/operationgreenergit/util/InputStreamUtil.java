package app.project.operationgreenergit.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static app.project.operationgreenergit.util.MessageTemplate.EXCEPTION_UTILITY_CLASS;

public final class InputStreamUtil {

	private InputStreamUtil() {
		throw new UnsupportedOperationException(EXCEPTION_UTILITY_CLASS.formatted(InputStreamUtil.class.getName()));
	}

	public static String readInputStream(InputStream inputStream) throws IOException {
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

}
