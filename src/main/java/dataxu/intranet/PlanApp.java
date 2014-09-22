package dataxu.intranet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ComponentScan
@EnableAutoConfiguration
@EnableJpaRepositories(basePackages = "dataxu.intranet.repository")
public class PlanApp {
    public static void main(String[] args) {
        SpringApplication.run(PlanApp.class, args);
    }
}