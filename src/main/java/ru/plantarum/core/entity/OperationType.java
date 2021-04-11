package ru.plantarum.core.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Класс для работы с таблицей operation_type
 *
 */
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OperationType {
    @Id
    @GeneratedValue(generator = "operation_type_gen")
    @SequenceGenerator(sequenceName = "operation_type_id_operation_type_seq",
            name = "operation_type_gen", allocationSize = 1)
    private Long idOperationType;

    @Size(max = 255)
    @NotBlank
    private String operationTypeName;

    @Size(max = 512)
    private String operationTypeComment;
}
