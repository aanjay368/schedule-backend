package id.my.schedule.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ValidatorService {

    @Autowired
    private Validator validator;

    public void validate(Object o){
        Set<ConstraintViolation<Object>> violations = validator.validate(o);
        if(!violations.isEmpty()){
            throw new ConstraintViolationException(violations);
        }
    }
}
