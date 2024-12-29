package io.github.manhnt217.task.play_ground.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author manhnguyen
 */
@RestController
public class Greeting {

	@GetMapping("/greeting")
	public String greeting() {
		return "Hello, World!";
	}
}
