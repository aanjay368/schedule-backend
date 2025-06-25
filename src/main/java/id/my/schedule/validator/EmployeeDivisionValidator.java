package id.my.schedule.validator;

import id.my.schedule.entity.EmployeeDivision;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class EmployeeDivisionValidator implements ConstraintValidator<NotExistEmployeeDivision, String> {
    @Override
    public void initialize(NotExistEmployeeDivision constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return Arrays.stream(EmployeeDivision.values()).anyMatch(employeePosition -> s.matches(employeePosition.name()));
    }
}
