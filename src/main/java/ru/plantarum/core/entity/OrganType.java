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
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;

/**
 * Класс для работы с таблицей organ_type
 */
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrganType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOrganType;

    @NotBlank
    @Size(max = 64)
    private String organTypeName;

    @Size(max = 255)
    private String organTypeComment;

    private OffsetDateTime inactive;
}
