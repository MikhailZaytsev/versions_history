package ru.plantarum.core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode (exclude = {"product", "campaign", "counterAgent"})
@Setter
@Getter
@ToString (exclude = {"product", "campaign", "counterAgent"} )
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"id_product", "id_counter_agent", "id_campaign"})})
public class PriceBuyPreliminarily {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPriceBuy;

    @NotNull(message = "Цена не должна быть пустой")
    @Min(0)
    private BigDecimal priceBuy;

    @Size(max = 255)
    private String priceBuyComment;

    @NotNull
    @Column(updatable = false)
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private OffsetDateTime priceBuyDate = OffsetDateTime.now();

    @ManyToOne()
    @JoinColumn(name = "id_product", nullable = false)
    private Product product;

    @ManyToOne()
    @JoinColumn(name = "id_campaign", nullable = false)
    private Campaign campaign;

    @ManyToOne()
    @JoinColumn(name = "id_counter_agent", nullable = false)
    private CounterAgent counterAgent;
}