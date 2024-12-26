package app.project.operationgreenergit.util;

public final class MessageTemplate {

	private MessageTemplate() {
		throw new UnsupportedOperationException(EXCEPTION_UTILITY_CLASS.formatted(MessageTemplate.class.getName()));
	}

	/**
	 * General placeholder rules:
	 * Single Argument Placeholder: '%s' indicates a single argument to be inserted into the template.
	 * Multiple Arguments Placeholder: %s indicates a list of arguments which are automatically formatted as [a, b, c].
	 */
	public static final String EXCEPTION_UTILITY_CLASS = "'%s' is a utility class and cannot be instantiated";
	public static final String EXCEPTION_CAUGHT = "Exception caught: '%s'";

	public static final String FILE_OPERATION_FAILED = "Failed to operate on file: '%s'";

	public static final String GIT_SYSTEM_VERSION = "System Git version: '%s'";

	public static final String MUST_NOT_BE_NULL = "'%s' must not be null";

	public static final String NOT_FOUND = "Not found: %s";

	public static final String PROCESS_START_FAILED = "Failed to start process: %s";
	public static final String PROCESS_INTERRUPTED = "Process interrupted: %s";
	public static final String PROCESS_CODE_EXIT = "Process: %s exit code: '%s'";
	public static final String PROCESS_CODE_ERROR_EXIT = "Process: %s exit code: '%s' with error: '%s'";

	public static final String REPOSITORY_NOT_FOUND = "Repository: '%s' not found";
	public static final String REPOSITORY_ALREADY_CLONED = "Repository: '%s' is already cloned";

	public static final String NON_SUPPORTED_OS = "Service does not support operating system: '%s'";

}
