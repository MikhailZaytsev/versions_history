package ru.plantarum.core.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class CounterAgentNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCounterAgentNote;

    @NotBlank(message = "Не должно быть пустым")
    @Size(max = 255)
    private String note;

    @ManyToOne()
    @JoinColumn(name = "id_counter_agent", nullable = false)
    @NotNull(message = "Необходимо выбрать контрагента")
    private CounterAgent counterAgent;
}
