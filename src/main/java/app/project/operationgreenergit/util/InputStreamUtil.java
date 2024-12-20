package app.project.operationgreenergit.util;

import app.project.operationgreenergit.exception.ExceptionSupplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static app.project.operationgreenergit.util.MessageTemplate.EXCEPTION_CAUGHT;
import static app.project.operationgreenergit.util.MessageTemplate.EXCEPTION_UTILITY_CLASS;
import static app.project.operationgreenergit.util.MessageTemplate.MUST_NOT_BE_NULL;

@Slf4j
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

	public static String readHandledInputStream(
			InputStream inputStream,
			ExceptionSupplier<? extends RuntimeException> ioExceptionSupplier) {
		Assert.notNull(ioExceptionSupplier, MUST_NOT_BE_NULL.formatted("ioExceptionSupplier"));

		try {
			return readInputStream(inputStream);
		} catch (IOException ex) {
			String reason = "Failed to read input stream";
			log.error(EXCEPTION_CAUGHT.formatted(reason), ex);
			throw ioExceptionSupplier.get(reason, ex);
		}
	}

}
