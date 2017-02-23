package at.ac.tuwien.infosys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by Kevin Bachmann on 27/10/2016.
 */
@SpringBootApplication
@EnableScheduling
public class HostmonitorApplication {

	public static void main(String[] args) {
		SpringApplication.run(HostmonitorApplication.class, args);
	}
}
