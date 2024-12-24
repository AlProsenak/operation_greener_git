package app.project.operationgreenergit.util;

import static app.project.operationgreenergit.util.MessageTemplate.EXCEPTION_UTILITY_CLASS;

public final class CommandTemplate {

	private CommandTemplate() {
		throw new UnsupportedOperationException(EXCEPTION_UTILITY_CLASS.formatted(CommandTemplate.class.getName()));
	}

	private static final String GIT_ADD_ALL = "git add -A";
	private static final String GIT_BRANCH_CREATE = "git branch %s";
	private static final String GIT_BRANCH_DELETE = "git branch -D %s";
	private static final String GIT_BRANCH_SWITCH = "git switch -f %s";
	private static final String GIT_CLONE = "git clone %s";
	private static final String GIT_COMMIT = "git commit -m \"%s\"";
	private static final String GIT_PUSH = "git push %s %s";
	private static final String GIT_VERSION = "git --version";

	private static final String MAKE_DIR = "mkdir -p %s";

}
