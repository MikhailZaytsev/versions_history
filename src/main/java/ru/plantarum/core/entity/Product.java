package ru.plantarum.core.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProduct;

    @NotNull
    private Long idTradeMark;

    @NotNull
    private Long idOrganType;

    private Short numberInPack;

    @NotBlank
    @Size(max = 255)
    private String productName;

    @Size(max = 255)
    private String productComment;

    private OffsetDateTime inactive;
}
