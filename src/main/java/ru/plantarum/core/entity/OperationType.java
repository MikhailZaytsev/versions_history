package ru.plantarum.core.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.Calendar;

/**
 * Класс для работы с таблицей operation_type
 *
 */
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class OperationType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOperationType;

    @Size(max = 64)
    @NotBlank
    @Column(unique = true)
    private String operationTypeName;

    @Size(max = 255)
    private String operationTypeComment;

}
