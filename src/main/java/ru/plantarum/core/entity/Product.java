package ru.plantarum.core.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

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
@EqualsAndHashCode(exclude = {"tradeMark", "organType"})
@Setter
@Getter
@ToString (exclude = {"tradeMark", "organType"})
public class Product {



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


//    @JsonBackReference
    @ManyToOne()
    @JoinColumn(name = "id_trade_mark", nullable = false)
    private TradeMark tradeMark;

//    @JsonBackReference
    @ManyToOne()
    @JoinColumn(name = "id_organ_type", nullable = false)
    private OrganType organType;
}
