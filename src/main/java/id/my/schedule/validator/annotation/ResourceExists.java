package id.my.schedule.validator.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

import id.my.schedule.validator.implementation.ResourceExistsValidator; // Lokasi Validator

@Documented
@Constraint(validatedBy = ResourceExistsValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER}) // Dapat digunakan pada field dan parameter
@Retention(RetentionPolicy.RUNTIME)
public @interface ResourceExists {

    String message() default "Sumber daya tidak ditemukan."; // Pesan default jika validasi gagal

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    // Properti tambahan untuk menentukan Repository mana yang akan digunakan
    Class<?> repository(); // Interface Repository (e.g., DivisionRepository.class)
    Class<?> entity() default Object.class; // Opsional: Tipe Entitas
}