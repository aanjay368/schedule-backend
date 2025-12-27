package id.my.schedule.validator.annotation;

import id.my.schedule.validator.implementation.ExistUsernameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD }) // Dapat digunakan pada field
@Retention(RetentionPolicy.RUNTIME) // Tersedia saat runtime
@Constraint(validatedBy = ExistUsernameValidator.class)
public @interface ExistUsername {

    String message() default "Nama pengguna sudah ada"; // Pesan error default

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}