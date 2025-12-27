package id.my.schedule.validator.annotation;

import id.my.schedule.validator.implementation.UpdatePasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UpdatePasswordValidator.class)
public @interface UpdatePassword {

    String message() default "Konfimasi Password tidak cocok";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
