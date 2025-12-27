package id.my.schedule.validator.implementation;

import id.my.schedule.validator.annotation.ResourceExists;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Optional;

@Component
public class ResourceExistsValidator implements ConstraintValidator<ResourceExists, Serializable> {

    private final ApplicationContext applicationContext;
    private Class<?> repositoryClass;

    public ResourceExistsValidator(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void initialize(ResourceExists constraintAnnotation) {
        this.repositoryClass = constraintAnnotation.repository();
    }

    @Override
    public boolean isValid(Serializable value, ConstraintValidatorContext context) {


        if (value == null) {
            return true;
        }

        Optional<?> repositoryOptional = applicationContext.getBeansOfType(repositoryClass).values().stream().findFirst();

        if (repositoryOptional.isEmpty()) {
            throw new IllegalStateException("Repository " + repositoryClass.getName() + " tidak ditemukan.");
        }

        JpaRepository<Object, Serializable> repository = (JpaRepository<Object, Serializable>) repositoryOptional.get();

        try {
            return repository.existsById(value);
        } catch (Exception e) {
            return false;
        }
    }
}