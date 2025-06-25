package id.my.schedule.validator;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = EmployeeDivisionValidator.class)
@Target({ ElementType.FIELD }) // Dapat digunakan pada field
@Retention(RetentionPolicy.RUNTIME) // Tersedia saat runtime
public @interface NotExistEmployeeDivision {

    String message() default "Division tidak valid"; // Pesan error default

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}