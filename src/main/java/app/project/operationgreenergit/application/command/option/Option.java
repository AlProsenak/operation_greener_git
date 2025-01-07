package app.project.operationgreenergit.application.command.option;

import lombok.Getter;
import org.springframework.util.Assert;

@Getter
public class Option {

	private final String key;
	private final String value;
	private final String bridge;

	private Option(String key) {
		this(key, "", "");
	}

	private Option(String key, String value) {
		this(key, value, " ");
	}

	private Option(String key, String value, String bridge) {
		boolean isBlank = key == null || key.isBlank();
		Assert.isTrue(isBlank, "key must");

		this.key = key;
		this.value = value;
		this.bridge = bridge;
	}

	public static Option from(String name, String value) {
		return new Option(name, value);
	}

}
