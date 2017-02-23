package at.ac.tuwien.infosys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by Kevin Bachmann on 14/11/2016.
 */
@SpringBootApplication
@EnableScheduling
public class CloudFogMiddleware {

    public static void main(String[] args) {
        SpringApplication.run(CloudFogMiddleware.class, args);
        System.out.println("  ____ _                 _       _____             __  __ _     _     _ _                             \n" +
                " / ___| | ___  _   _  __| |     |  ___|__   __ _  |  \\/  (_) __| | __| | | _____      ____ _ _ __ ___ \n" +
                "| |   | |/ _ \\| | | |/ _` |_____| |_ / _ \\ / _` | | |\\/| | |/ _` |/ _` | |/ _ \\ \\ /\\ / / _` | '__/ _ \\\n" +
                "| |___| | (_) | |_| | (_| |_____|  _| (_) | (_| | | |  | | | (_| | (_| | |  __/\\ V  V / (_| | | |  __/\n" +
                " \\____|_|\\___/ \\__,_|\\__,_|     |_|  \\___/ \\__, | |_|  |_|_|\\__,_|\\__,_|_|\\___| \\_/\\_/ \\__,_|_|  \\___|\n" +
                "                                           |___/                                                      ");


    }
}