package id.my.schedule.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

@Component
public class StringToLocalDateConverter implements Converter<String, LocalDate> {


    @Override
    public LocalDate convert(String source) {

        if (source.equalsIgnoreCase("null") || source.equalsIgnoreCase("undefined")) {
            return LocalDate.now(ZoneId.of("Asia/Makassar"));
        }

        try {
            return LocalDate.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.of("id", "ID")));
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tanggal tidak valid");
        }
    }
}
