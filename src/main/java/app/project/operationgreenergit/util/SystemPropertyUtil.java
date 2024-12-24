package app.project.operationgreenergit.util;

import static app.project.operationgreenergit.util.MessageTemplate.EXCEPTION_UTILITY_CLASS;

public final class SystemPropertyUtil {

	private SystemPropertyUtil() {
		throw new UnsupportedOperationException(EXCEPTION_UTILITY_CLASS.formatted(SystemPropertyUtil.class.getName()));
	}

	public static final String OS_NAME = System.getProperty("os.name");
	public static final String OS_LOWER_CASE_NAME = OS_NAME.toLowerCase();

	public static boolean isLinux() {
		return OS_LOWER_CASE_NAME.contains("linux");
	}

	public static boolean isMacOS() {
		return OS_LOWER_CASE_NAME.contains("mac");
	}

	public static boolean isSupportedOS() {
		return isLinux() || isMacOS();
	}

}
