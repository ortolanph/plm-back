package pt.pauloortolan.plm_back;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.scheduling.annotation.*;

@SpringBootApplication
@EnableScheduling
public class PlmBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlmBackApplication.class, args);
    }

}
