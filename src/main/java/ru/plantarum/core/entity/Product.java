package ru.plantarum.core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

/**
 * Класс для работы с таблицей product
 */

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode (exclude = {"tradeMark", "organType", "operationRows", "bareCodes", "priceBuyPreliminarilies", "priceSales"})
@Setter
@Getter
@ToString (exclude = {"tradeMark", "organType", "operationRows", "bareCodes", "priceBuyPreliminarilies", "priceSales"})
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"numberInPack", "id_trade_mark", "productName"})})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProduct;

    @Min(1)
    private Short numberInPack;

    @NotBlank(message = "Название не должно быть пустым")
    @Size(max = 255)
    private String productName;

    @Size(max = 255)
    private String productComment;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private OffsetDateTime inactive;


//    @JsonBackReference
    @ManyToOne()
    @JoinColumn(name = "id_trade_mark", nullable = false)
    @NotNull(message = "Необходимо выбрать торговую марку")
  // @EqualsAndHashCode.Exclude
    private TradeMark tradeMark;

//    @JsonBackReference
    @ManyToOne()
    @JoinColumn(name = "id_organ_type", nullable = false)
    @NotNull(message = "Необходимо выбрать тип органа")
    //@EqualsAndHashCode.Exclude
    private OrganType organType;

    @JsonIgnore
    @OneToMany(mappedBy = "product")
    private Set<OperationRow> operationRows;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "product")
    private Set<BareCode> bareCodes;

    @JsonIgnore
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "product")
    private Set<PriceBuyPreliminarily> priceBuyPreliminarilies;

    @JsonIgnore
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "product")
    private Set<PriceSale> priceSales;
//    @EqualsAndHashCode.Include
//    public Long getIdTradeMark(){
//        return tradeMark.getIdTradeMark();
//    }

}
