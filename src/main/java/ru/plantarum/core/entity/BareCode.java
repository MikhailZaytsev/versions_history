package ru.plantarum.core.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "product")
@Setter
@Getter
@ToString (exclude = "product")
public class BareCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idBareCode;

    @NotNull
    private BigDecimal ean_13;

    @Size(max = 255)
    private String bareCodeComment;

    @ManyToOne()
    @JoinColumn(name = "id_product", nullable = false)
    @NotNull(message = "необходимо выбрать тип контрагента")
    private Product product;
}
