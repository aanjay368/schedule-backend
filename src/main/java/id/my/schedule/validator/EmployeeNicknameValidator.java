package id.my.schedule.validator;

import id.my.schedule.repository.EmployeeRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class EmployeeNicknameValidator implements ConstraintValidator<ExistEmployeeNickname, String> {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public void initialize(ExistEmployeeNickname constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return  employeeRepository.existsByNickname(s) == 0;
    }
}
