package id.my.schedule.validator;

import jakarta.validation.ConstraintValidator;
import id.my.schedule.entity.EmployeePosition;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class EmployeePoisitionValidator implements ConstraintValidator<NotExistEmployeePosition, String> {
    @Override
    public void initialize(NotExistEmployeePosition constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return Arrays.stream(EmployeePosition.values()).anyMatch(employeePosition -> s.matches(employeePosition.name()));
    }
}
