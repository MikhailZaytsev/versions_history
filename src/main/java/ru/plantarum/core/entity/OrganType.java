package ru.plantarum.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

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
@Getter
@Setter
@EqualsAndHashCode (exclude = {"productSet"})
@ToString (exclude = {"productSet"})
public class OrganType {

    @JsonIgnore
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
