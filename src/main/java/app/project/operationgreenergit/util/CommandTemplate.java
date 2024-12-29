package app.project.operationgreenergit.util;

import static app.project.operationgreenergit.util.MessageTemplate.EXCEPTION_UTILITY_CLASS;

public final class CommandTemplate {

	private CommandTemplate() {
		throw new UnsupportedOperationException(EXCEPTION_UTILITY_CLASS.formatted(CommandTemplate.class.getName()));
	}

	private static final String AND = " && ";
	private static final String END = "; ";
	private static final String OR = " || ";

	public static final String GIT_ADD_ALL = "git add -A";
	public static final String GIT_BRANCH_CREATE = "git branch %s";
	public static final String GIT_BRANCH_DELETE = "git branch -D %s";
	public static final String GIT_BRANCH_SWITCH = "git switch -f %s";
	public static final String GIT_CLEAN = "git clean -f";
	public static final String GIT_CLONE = "git clone %s";
	public static final String GIT_COMMIT = "git commit -m \"%s\"";
	public static final String GIT_PUSH = "git push -f %s %s";
	public static final String GIT_VERSION = "git --version";

	public static final String MAKE_DIR = "mkdir -p %s";

	public static final String GIT_BRANCH_RECREATE = GIT_BRANCH_DELETE + END + GIT_BRANCH_CREATE;
	public static final String GIT_BRANCH_SWITCH_TO_RECREATED = GIT_BRANCH_RECREATE + AND + GIT_BRANCH_SWITCH;

}
