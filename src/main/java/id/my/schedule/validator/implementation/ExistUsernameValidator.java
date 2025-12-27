package id.my.schedule.validator.implementation;

import id.my.schedule.repository.UserRepository;
import id.my.schedule.validator.annotation.ExistUsername;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

public class ExistUsernameValidator implements ConstraintValidator<ExistUsername, String> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
      if(Objects.nonNull(s)) {
          return !userRepository.existsByUsername(s.toLowerCase().replace(" ", "-"));
      }
      return true;
    }
}
