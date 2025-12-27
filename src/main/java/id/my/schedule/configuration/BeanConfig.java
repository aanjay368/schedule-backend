package id.my.schedule.configuration;

import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.VirtualThreadTaskExecutor;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.Executor;

@Configuration
public class BeanConfig {

    @Bean
    public Executor taskExecutor() {
        return new VirtualThreadTaskExecutor();
    }

    @Bean
    public DateTimeFormatter dateTimeFormatter() {
        return DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.of("id", "ID"));
    }

    @Bean
    public InMemoryHttpExchangeRepository inMemoryHttpExchangeRepository() {

        return new InMemoryHttpExchangeRepository();
    }
}
