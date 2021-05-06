package ru.plantarum.core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

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
@EqualsAndHashCode (exclude = {"products"})
@Setter
@Getter
@ToString(exclude = {"products"})
public class TradeMark {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTradeMark;

    @NotBlank
    @Size(max = 64)
    @Column(unique = true)
    private String tradeMarkName;

    @Size(max = 255)
    private String tradeMarkComment;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private OffsetDateTime inactive;

    @JsonIgnore
    @OneToMany(mappedBy = "tradeMark")
    private Set<Product> products;
}
