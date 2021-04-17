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
 * Класс для работы с таблицей organ_type
 */
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrganType {

    @OneToMany(mappedBy = "organType")
    private Set<Product> products;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOrganType;

    @NotBlank
    @Size(max = 64)
    @Column(unique = true)
    private String organTypeName;

    @Size(max = 255)
    private String organTypeComment;

    private OffsetDateTime inactive;
}
