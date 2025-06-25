package id.my.schedule.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = EmployeePoisitionValidator.class)
@Target({ ElementType.FIELD }) // Dapat digunakan pada field
@Retention(RetentionPolicy.RUNTIME) // Tersedia saat runtime
public @interface NotExistEmployeePosition {

    String message() default "Posisi tidak valid"; // Pesan error default

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}