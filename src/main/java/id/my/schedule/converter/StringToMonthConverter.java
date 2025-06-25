package id.my.schedule.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;


import java.time.Month;
import java.time.MonthDay;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;

@Component
public class StringToMonthConverter implements Converter<String, Month> {

    @Override
    public Month convert(String source) {
        if (source.equalsIgnoreCase("null") || source.equalsIgnoreCase("undefined")) {
            return MonthDay.now(ZoneId.of("Asia/Makassar")).getMonth();
        }

        try {
            return Month.of(Integer.parseInt(source));
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bulan tidak valid");
        }
    }
}
