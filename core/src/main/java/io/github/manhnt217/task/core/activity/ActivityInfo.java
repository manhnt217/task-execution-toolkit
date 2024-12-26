package io.github.manhnt217.task.core.activity;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

/**
 * @author manhnguyen
 */
public class ActivityInfo {

	@Getter
	private final String name;
	@Getter
	private final boolean registerOutput;

	@Getter @Setter
	private OffsetDateTime startTime;
	@Getter @Setter
	private OffsetDateTime endTime;
	@Setter
	private boolean hasOutput;

	private ActivityInfo(String name, boolean registerOutput) {
		this.name = name;
		this.registerOutput = registerOutput;
	}

	public static ActivityInfo from(Activity activity) {
		return new ActivityInfo(activity.getName(), activity.registerOutput());
	}

	public boolean hasOutput() {
		return hasOutput;
	}

}
