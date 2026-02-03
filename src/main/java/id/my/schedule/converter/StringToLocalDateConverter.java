package id.my.schedule.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class StringToLocalDateConverter implements Converter<String, LocalDate> {

    @Override
    public LocalDate convert(String source) {
        if (source == null || source.isBlank() ||
                source.equalsIgnoreCase("null") ||
                source.equalsIgnoreCase("undefined")) {
            return null;
        }

        try {
            System.out.println(source);
            return LocalDate.parse(source, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
