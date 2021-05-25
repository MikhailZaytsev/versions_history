package ru.plantarum.core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode (exclude = {"operationType", "counterAgent", "operationListStatus", "operationRows"})
@Setter
@Getter
@ToString (exclude = {"operationType", "counterAgent", "operationListStatus", "operationRows"})
public class OperationList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOperationList;

    @Size(max = 255)
    private String operationListComment;

    @NotNull
    @Column(updatable = false)
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private OffsetDateTime operationListDate = OffsetDateTime.now();

    @ManyToOne()
    @JoinColumn(name = "id_operation_type", nullable = false)
    @NotNull(message = "Необходимо выбрать тип операции")
    private OperationType operationType;

    @ManyToOne()
    @JoinColumn(name = "id_counter_agent", nullable = false)
    @NotNull(message = "Необходимо выбрать контрагента")
    private CounterAgent counterAgent;

    @ManyToOne()
    @JoinColumn(name = "id_operation_list_status", nullable = false)
    @NotNull(message = "Необходимо выбрать статус операции")
    private OperationListStatus operationListStatus;

    @JsonIgnore
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "operationList")
    private List<OperationRow> operationRows;
}
