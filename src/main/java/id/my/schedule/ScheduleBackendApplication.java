package id.my.schedule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.Executor;

@SpringBootApplication
@EnableAsync
@EnableJpaRepositories
@EnableJpaAuditing
public class ScheduleBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScheduleBackendApplication.class, args);
	}

	@Bean
	public Executor taskExecutor(){
		return  new VirtualThreadTaskExecutor();
	}

	@Bean
	public DateTimeFormatter dateTimeFormatter(){
		return DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.of("id", "ID"));
	}

	@Bean
	public InMemoryHttpExchangeRepository inMemoryHttpExchangeRepository(){

		return new InMemoryHttpExchangeRepository();
	}
}
