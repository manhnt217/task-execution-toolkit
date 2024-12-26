package io.github.manhnt217.task.sample.test.helper;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author manhnguyen
 */
@Getter
@Setter
@Accessors(chain = true)
public class SampleOutput {
	private String category;
	private boolean important;
	private double rate;
}
