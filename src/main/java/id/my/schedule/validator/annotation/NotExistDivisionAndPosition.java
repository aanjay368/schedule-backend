package id.my.schedule.validator.annotation;

import id.my.schedule.validator.implementation.NotExistDivisionAndPositionValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE }) // Dapat digunakan pada field
@Retention(RetentionPolicy.RUNTIME) // Tersedia saat runtime
@Constraint(validatedBy = NotExistDivisionAndPositionValidator.class)
public @interface NotExistDivisionAndPosition {

    String message() default "Posisi tidak valid dengan Divisi"; // Pesan error default

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String field() default "position";

}
