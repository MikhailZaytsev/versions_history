package ru.plantarum.core.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode (exclude = {"product", "operationList"})
@Setter
@Getter
@ToString (exclude = {"operationList", "product"})
public class OperationRow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOperationRow;

    @NotNull(message = "Цена не должна быть пустой")
    @Min(0)
    private BigDecimal operationPrice;

    @NotNull(message = "Количество не должно быть пустым")
    @Min(1)
    private short quantity;

    @Size(max = 255)
    private String operationRowComment;

    @ManyToOne()
    @JoinColumn(name = "id_operation_list", nullable = false)
    @NotNull(message = "Строка не привязана к документы")
    private OperationList operationList;

    @ManyToOne()
    @JoinColumn(name = "id_product", nullable = false)
    @NotNull(message = "Необходимо выбрать товар")
    private Product product;
}
