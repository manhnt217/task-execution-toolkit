package io.github.manhnt217.task.sample.test.helper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author manhnguyen
 */
@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
@ToString
public class SampleInput {
	private String name;
	private int age;
	private String address;
}
