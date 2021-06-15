package ru.plantarum.core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode (exclude = {"counterAgentType", "counterAgentNotes", "operationLists", "priceBuyPreliminarilies"})
@Setter
@Getter
@ToString (exclude = {"counterAgentType", "counterAgentNotes", "operationLists", "priceBuyPreliminarilies"})
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"counterAgentName", "counterAgentProfile", "counterAgentProfile"})})
public class CounterAgent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCounterAgent;

    @Size(max = 64)
    @NotBlank(message = "Имя контрагента не должно быть пустым")
    private String counterAgentName;

    @Size(max = 127)
    @NotBlank(message = "Профиль не должен быть пустым")
    private String counterAgentProfile;

    @Size(max = 32)
    @NotBlank(message = "Телефон не может быть пустым")
    private String counterAgentPhone;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private OffsetDateTime inactive;

    @ManyToOne()
    @JoinColumn(name = "id_counter_agent_type", nullable = false)
    @NotNull(message = "необходимо выбрать тип контрагента")
    private CounterAgentType counterAgentType;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "counterAgent")
    private List<CounterAgentNote> counterAgentNotes;

    @JsonIgnore
    @OneToMany(mappedBy = "counterAgent")
    private Set<OperationList> operationLists;

    @JsonIgnore
    @OneToMany(mappedBy = "counterAgent")
    private Set<PriceBuyPreliminarily> priceBuyPreliminarilies;
}
