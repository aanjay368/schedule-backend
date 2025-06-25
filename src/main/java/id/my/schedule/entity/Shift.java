package id.my.schedule.entity;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public enum Shift {

    P,
    O,
    S,
    S1,
    S2,
    L;


    public static Shift safeValueOf(String value) {

        if (value.replace(" ", "").equalsIgnoreCase("P")) {
            return Shift.P;
        } else if (value.replace(" ", "").equalsIgnoreCase("O")) {
            return Shift.O;
        } else if (value.replace(" ", "").equalsIgnoreCase("S")) {
            return Shift.S;
        } else if (value.replace(" ", "").equalsIgnoreCase("S1")) {
            return Shift.S1;
        } else if (value.replace(" ", "").equalsIgnoreCase("S2")) {
            return Shift.S2;
        } else if (value.replace(" ", "").equalsIgnoreCase("L")) {
            return Shift.L;
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Can't found shift with value " + value);
    }

}
