package ru.plantarum.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

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
@EqualsAndHashCode (exclude = "operationLists")
@ToString (exclude = "operationLists")
public class OperationType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOperationType;

    @Size(max = 64)
    @NotBlank(message = "Название не должно быть пустым")
    @Column(unique = true)
    private String operationTypeName;

    @Size(max = 255)
    private String operationTypeComment;

    @JsonIgnore
    @OneToMany(mappedBy = "operationType")
    private Set<OperationList> operationLists;
}