package id.my.schedule.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD }) // Dapat digunakan pada field
@Retention(RetentionPolicy.RUNTIME) // Tersedia saat runtime
@Constraint(validatedBy = EmployeeNicknameValidator.class)
public @interface ExistEmployeeNickname {

    String message() default "Nama panggilan sudah ada"; // Pesan error default

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}