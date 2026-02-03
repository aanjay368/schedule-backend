package id.my.schedule.service.implementation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component

public class ValidatorService {

    @Autowired
    private Validator validator;

    public void validate(Object o) {
        Set<ConstraintViolation<Object>> violations = validator.validate(o);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    public void validateAnyAttribute(Object o, String... attributeNames) {
        Set<ConstraintViolation<Object>> allViolations = new HashSet<>();
        for (String attributeName : attributeNames) {
            Set<ConstraintViolation<Object>> violations = validator.validateProperty(o, attributeName);
            allViolations.addAll(violations);
        }

        if (!allViolations.isEmpty()) {
            throw new ConstraintViolationException(allViolations);
        }
    }
}
