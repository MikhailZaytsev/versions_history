package ru.plantarum.core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.*;
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

    @NotNull(message = "Значение цены не должно быть пустым")
    @DecimalMin(value = "0.00", inclusive = false, message = "Цена не может быть отрицательной или нулевой")
    @Digits(integer=17, fraction=2)
    private BigDecimal priceBuy;

    @Size(max = 255)
    private String priceBuyComment;

    @NotNull
    @Column(updatable = false)
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private OffsetDateTime priceBuyDate = OffsetDateTime.now();

    @ManyToOne()
    @JoinColumn(name = "id_product", nullable = false)
    @NotNull(message = "Значение продукта не должно быть пустым")
    private Product product;

    @ManyToOne()
    @JoinColumn(name = "id_campaign", nullable = false)
    @NotNull(message = "необходимо выбрать компанию")
    private Campaign campaign;

    @ManyToOne()
    @JoinColumn(name = "id_counter_agent", nullable = false)
    @NotNull(message = "необходимо выбрать контрагента")
    private CounterAgent counterAgent;
}
