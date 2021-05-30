package ru.plantarum.core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode (exclude = {"product", "campaign"})
@Setter
@Getter
@ToString (exclude = {"product", "campaign"})
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"id_product", "propertyPrice", "id_campaign"})})
public class PriceSale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPriceSale;

    @NotNull(message = "Цена не должна быть пустой")
    @Min(0)
    private BigDecimal price;

    @NotBlank
    @Size(max = 64)
    private String propertyPrice;

    @NotNull
    private boolean packOnly;

    @Size(max = 255)
    private String priceSaleComment;

    @NotNull
    @Column(updatable = false)
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private OffsetDateTime priceSaleDate = OffsetDateTime.now();

    @ManyToOne()
    @JoinColumn(name = "id_product", nullable = false)
    private Product product;

    @ManyToOne()
    @JoinColumn(name = "id_campaign", nullable = false)
    private Campaign campaign;
}
