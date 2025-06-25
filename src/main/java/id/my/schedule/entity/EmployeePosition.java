package id.my.schedule.entity;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public enum EmployeePosition {

    WORKER,LEADER,DEVELOPER;

    public static EmployeePosition safeValueOf(String value) {
        for (EmployeePosition position : EmployeePosition.values()){
            if (position.name().equalsIgnoreCase(value)){
                return EmployeePosition.valueOf(value);
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Can't found position with value " + value);
    }
}
