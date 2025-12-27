package id.my.schedule.validator.implementation;

import id.my.schedule.model.user.UpdateUserRequest;
import id.my.schedule.repository.UserRepository;
import id.my.schedule.validator.annotation.UpdatePassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

public class UpdatePasswordValidator implements ConstraintValidator<UpdatePassword, UpdateUserRequest> {

    @Autowired
    private UserRepository userRepository;


    @Override
    public boolean isValid(UpdateUserRequest request, ConstraintValidatorContext context) {

        boolean isChangingPassword = Objects.nonNull(request.getOldPassword()) ||
                Objects.nonNull(request.getNewPassword()) ||
                Objects.nonNull(request.getConfirmPassword());

        // 2. Jika tidak ada field password yang diisi (misal hanya update username)
        // Langsung kembalikan true agar validasi dianggap sukses (skip)
        if (!isChangingPassword) {
            return true;
        }

        if(!BCrypt.checkpw(request.getOldPassword(), request.getUser().getPassword())){
            addError(context, "oldPassword", "Password lama salah");
            return false;
        }

        if (BCrypt.checkpw(request.getNewPassword(), request.getUser().getPassword())) {
            addError(context, "newPassword", "Password baru tidak boleh sama dengan Password lama");
            return false;
        }

        if (!request.getConfirmPassword().equals(request.getNewPassword())) {
            addError(context, "confirmPassword", "Konfirmasi Password tidak cocok");
            return false;
        }
        return true;
    }

    private void addError(ConstraintValidatorContext context, String propretuNode, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(propretuNode)
                .addConstraintViolation();
    }

}
