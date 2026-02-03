package id.my.schedule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.Executor;

@SpringBootApplication
@EnableAsync
@EnableJpaRepositories
@EnableJpaAuditing
@EnableScheduling
public class ScheduleBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScheduleBackendApplication.class, args);
	}


}
