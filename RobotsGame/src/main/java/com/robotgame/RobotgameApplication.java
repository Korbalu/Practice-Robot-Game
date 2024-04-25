package com.robotgame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RobotgameApplication {

	public static void main(String[] args) {
		SpringApplication.run(RobotgameApplication.class, args);
	}

}
