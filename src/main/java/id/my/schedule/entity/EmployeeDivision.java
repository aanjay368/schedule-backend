package id.my.schedule.entity;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public enum EmployeeDivision {

    AIC,PORTER;

    public static EmployeeDivision safeValueOf(String value) {
        for (EmployeeDivision division : EmployeeDivision.values()){
            if (division.name().equalsIgnoreCase(value)){
                return EmployeeDivision.valueOf(value);
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Can't found division with value " + value);
    }
}
