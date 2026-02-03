package id.my.schedule.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToIntegerConverter implements Converter<String, Integer> {

    @Override
    public Integer convert(String source) {
        if (source == null || source.isBlank() ||
                source.equalsIgnoreCase("null") ||
                source.equalsIgnoreCase("undefined")) {
            return null;
        }

        try {
            return Integer.valueOf(source.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
