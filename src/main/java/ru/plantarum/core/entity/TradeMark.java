package ru.plantarum.core.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.Set;

/**
 * класс для работы с таблицей trade_mark
 */

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TradeMark {

    @OneToMany(mappedBy = "tradeMark")
    private Set<Product> products;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTradeMark;

    @NotBlank
    @Size(max = 64)
    private String tradeMarkName;

    @Size(max = 255)
    private String tradeMarkComment;

    private OffsetDateTime inactive;

}
