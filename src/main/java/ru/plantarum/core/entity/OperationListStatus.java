package ru.plantarum.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode (exclude = "operationLists")
@Setter
@Getter
@ToString (exclude = "operationLists")
public class OperationListStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOperationListStatus;

    @NotBlank (message = "Название статуса не должно быть пустым")
    @Size(max = 64)
    private String operationListStatusName;

    @NotNull (message = "Выберите тип статуса")
    private boolean inactive;

    @JsonIgnore
    @OneToMany(mappedBy = "operationListStatus")
    private Set<OperationList> operationLists;



}
