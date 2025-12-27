package id.my.schedule.validator.implementation;

import id.my.schedule.entity.Division;
import id.my.schedule.entity.Position;
import id.my.schedule.repository.DivisionRepository;
import id.my.schedule.validator.annotation.NotExistDivisionAndPosition;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

public class NotExistDivisionAndPositionValidator implements ConstraintValidator<NotExistDivisionAndPosition, Object> {

    @Autowired
    private DivisionRepository divisionRepository;

    @SneakyThrows
    @Override
    public boolean isValid(Object o, ConstraintValidatorContext context) {

        Field divisionField = o.getClass().getDeclaredField("divisionId");
        divisionField.setAccessible(true);
        Integer divisionId = (Integer) divisionField.get(o);
        divisionField.setAccessible(false);

        Field positionField = o.getClass().getDeclaredField("positionId");
        positionField.setAccessible(true);
        Integer positionId = (Integer) positionField.get(o);
        positionField.setAccessible(false);

        if (divisionId == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Divisi tidak boleh kosong")
                    .addPropertyNode("divisionId")
                    .addConstraintViolation();
            return false;
        }

        if (positionId == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Posisi tidak boleh kosong")
                    .addPropertyNode("positionId")
                    .addConstraintViolation();
            return false;
        }


        Division division = divisionRepository.findById(divisionId).orElse(null);

        if (division == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Divisi tidak ditemukan")
                    .addPropertyNode("divisionId")
                    .addConstraintViolation();
            return false;
        }

        List<Position> positions = division.getPositions().stream().filter(position -> Objects.equals(position.getId(), positionId)).toList();

        if (positions.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Posisi tidak valid dengan Divisi")
                    .addPropertyNode("positionId")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
