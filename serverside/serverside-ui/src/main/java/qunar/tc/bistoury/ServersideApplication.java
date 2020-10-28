package qunar.tc.bistoury;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = "qunar.tc.bistoury")
public class ServersideApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServersideApplication.class);
    }
}
