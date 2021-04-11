package ru.plantarum.core.entity;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class OperationTypeTest {

    private static Validator validator;

    @BeforeAll
    public static void setupValidatorInstance() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void whenNotNullName_thenNoConstraintViolations() {
        OperationType operationTypeNotNull = OperationType.builder()
                .operationTypeName("Not null")
                .build();
        Set<ConstraintViolation<OperationType>> violations = validator.validate(operationTypeNotNull);

        assertThat(violations.size()).isEqualTo(0);
    }

    @Test
    public void whenNullName_thenOneConstraintViolation() {
        OperationType operationType = OperationType.builder().build();
        Set<ConstraintViolation<OperationType>> violations = validator.validate(operationType);
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    public void whenEmptyName_thenOneConstraintViolation() {
        OperationType operationType = OperationType.builder()
                .operationTypeName("")
                .build();
        Set<ConstraintViolation<OperationType>> violations = validator.validate(operationType);
        assertThat(violations.size()).isEqualTo(1);
    }

}