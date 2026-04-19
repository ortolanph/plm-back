package pt.pauloortolan.plm_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PlmBackApplication {

	static void main(String[] args) {
		SpringApplication.run(PlmBackApplication.class, args);
	}

}
