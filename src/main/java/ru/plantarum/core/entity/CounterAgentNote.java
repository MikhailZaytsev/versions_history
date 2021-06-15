package ru.plantarum.core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode (exclude = "counterAgent")
@ToString (exclude = "counterAgent")
public class CounterAgentNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCounterAgentNote;

    @NotBlank(message = "Не должно быть пустым")
    @Size(max = 255)
    private String note;

    @NotNull
    @Column(updatable = false)
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private OffsetDateTime noteDate = OffsetDateTime.now();

    @ManyToOne()
    @JoinColumn(name = "id_counter_agent", nullable = false)
    @NotNull(message = "Необходимо выбрать контрагента")
    @JsonIgnore
    private CounterAgent counterAgent;
}
