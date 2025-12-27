package id.my.schedule.model;

import id.my.schedule.entity.Division;
import id.my.schedule.entity.Employee;
import id.my.schedule.entity.Position;
import id.my.schedule.entity.Shift;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.YearMonth;
import java.util.Map;

@AllArgsConstructor
@Getter
public class ScheduleMappingContext {

    private Division division;

    private Position position;

    private YearMonth yearMonth;

    private Map<String, Employee> employeeMap;

    private Map<String, Shift> shiftMap;

}
