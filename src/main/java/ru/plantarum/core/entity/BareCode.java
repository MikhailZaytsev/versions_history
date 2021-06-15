package ru.plantarum.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"ean_13", "id_product"})})
public class BareCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idBareCode;

    @NotNull
    private BigDecimal ean_13;

    @ManyToOne()
    @JoinColumn(name = "id_product", nullable = false)
    @NotNull(message = "необходимо выбрать продукт")
    @JsonIgnore
    private Product product;
}
