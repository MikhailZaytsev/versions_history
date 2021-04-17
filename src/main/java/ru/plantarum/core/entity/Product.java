package ru.plantarum.core.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;

/**
 * Класс для работы с таблицей product
 */
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Product {

    @ManyToOne()
    @JoinColumn(name = "id_trade_mark", nullable = false)
    private TradeMark tradeMark;

    @ManyToOne()
    @JoinColumn(name = "id_organ_type", nullable = false)
    private OrganType organType;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProduct;

    @Positive
    private Short numberInPack;

    @NotBlank
    @Size(max = 255)
    @Column(unique = true)
    private String productName;

    @Size(max = 255)
    private String productComment;

    private OffsetDateTime inactive;
}
