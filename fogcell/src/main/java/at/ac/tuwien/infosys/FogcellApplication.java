package at.ac.tuwien.infosys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by Kevin Bachmann on 27/10/2016.
 */
@SpringBootApplication
@EnableScheduling
public class FogcellApplication {

	public static void main(String[] args) {
		SpringApplication.run(FogcellApplication.class, args);
		System.out.println(" _____              ____     _ _ \n" +
				"|  ___|__   __ _   / ___|___| | |\n" +
				"| |_ / _ \\ / _` | | |   / _ \\ | |\n" +
				"|  _| (_) | (_| | | |__|  __/ | |\n" +
				"|_|  \\___/ \\__, |  \\____\\___|_|_|\n" +
				"           |___/                 \n");


	}
}
