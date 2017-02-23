package at.ac.tuwien.infosys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by Kevin Bachmann on 27/10/2016.
 */
@SpringBootApplication
@EnableScheduling
public class FogcontrolnodeApplication {

	public static void main(String[] args) {
		SpringApplication.run(FogcontrolnodeApplication.class, args);
		System.out.println(" _____              ____            _             _   _   _           _      \n" +
				"|  ___|__   __ _   / ___|___  _ __ | |_ _ __ ___ | | | \\ | | ___   __| | ___ \n" +
				"| |_ / _ \\ / _` | | |   / _ \\| '_ \\| __| '__/ _ \\| | |  \\| |/ _ \\ / _` |/ _ \\\n" +
				"|  _| (_) | (_| | | |__| (_) | | | | |_| | | (_) | | | |\\  | (_) | (_| |  __/\n" +
				"|_|  \\___/ \\__, |  \\____\\___/|_| |_|\\__|_|  \\___/|_| |_| \\_|\\___/ \\__,_|\\___|\n" +
				"           |___/                                                             \n");
	}
}
