package id.my.schedule.converter;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.core.convert.converter.Converter;

import java.time.LocalTime;

public class StringToLocalTimeConverter implements Converter<String, LocalTime> {

    @Override
    public LocalTime convert(String source) {
        try {
            return LocalTime.parse(source);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
