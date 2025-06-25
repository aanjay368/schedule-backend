package id.my.schedule.converter;


import id.my.schedule.entity.EmployeeDivision;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToEmployeeDivisionConverter implements Converter<String, EmployeeDivision> {
    @Override
    public EmployeeDivision convert(String source) {
        return EmployeeDivision.safeValueOf(source);
    }
}
