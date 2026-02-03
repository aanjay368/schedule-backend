package id.my.schedule.validator.annotation;

import id.my.schedule.validator.implementation.SubmissionValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE}) // Berlaku untuk Class
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SubmissionValidator.class) // Link ke logic validator
@Documented
public @interface ValidSubmission {
    String message() default "Pengajuan tidak valid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
