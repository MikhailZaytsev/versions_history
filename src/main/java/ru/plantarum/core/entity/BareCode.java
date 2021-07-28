package ru.plantarum.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "product")
@Setter
@Getter
@ToString (exclude = "product")
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"ean13", "id_product"})})
public class BareCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idBareCode;

    @NotBlank
    @Pattern(regexp="^\\d{13}$", message = "В поле \"штрих-код\" должно быть 13 цифр")
    private String ean13;

    @ManyToOne()
    @JoinColumn(name = "id_product", nullable = false)
    @NotNull(message = "Значение продукта не должно быть пустым")
    @JsonIgnore
    private Product product;
}
