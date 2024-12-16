package app.project.operationgreenergit.util;

public final class MessageTemplate {

	private MessageTemplate() {
		throw new UnsupportedOperationException(EXCEPTION_UTILITY_CLASS.formatted(MessageTemplate.class.getName()));
	}

	public static final String EXCEPTION_UTILITY_CLASS = "%s is a utility class and cannot be instantiated";
	public static final String EXCEPTION_CAUGHT = "Exception caught: %s";

	public static final String PROCESS_START_FAILED = "Failed to start process: %s";
	public static final String PROCESS_INTERRUPTED = "Process interrupted: %s";
	public static final String PROCESS_CODE_EXIT = "Process: %s exit code: %s";
	public static final String PROCESS_CODE_ERROR_EXIT = "Process: %s exit code: %s with error: %s";

}
