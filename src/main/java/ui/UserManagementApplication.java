package ui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@SpringBootApplication
@ComponentScan(basePackages = {"validation", "configuration", "deleteUserId", "model", "repository",
    "ui", "resource", "service", "advice", "annotation"})
@EntityScan(basePackages = {"repository", "model", "service", "configuration", "ui"})
public class UserManagementApplication {

  public static void main(String[] args) {
    SpringApplication.run(UserManagementApplication.class, args);
  }

}
