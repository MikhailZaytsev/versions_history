package ru.plantarum.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(exclude = {"counterAgents"})
@ToString(exclude = {"counterAgents"})
public class CounterAgentType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCounterAgentType;

    @Size(max = 64)
    @NotBlank(message = "Название не должно быть пустым")
    @Column(unique = true)
    private String counterAgentTypeName;

    @Size(max = 255)
    private String counterAgentTypeComment;

    @JsonIgnore
    @OneToMany(mappedBy = "counterAgentType")
    private Set<CounterAgent> counterAgents;
}
