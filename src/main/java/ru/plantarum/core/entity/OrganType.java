package ru.plantarum.core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@EqualsAndHashCode (exclude = {"products"})
@ToString (exclude = {"products"})
public class OrganType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOrganType;

    @NotBlank(message = "Название не должно быть пустым")
    @Size(max = 64)
    @Column(unique = true)
    private String organTypeName;

    @Size(max = 255)
    private String organTypeComment;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private OffsetDateTime inactive;

    @JsonIgnore
    @OneToMany(mappedBy = "organType")
    private Set<Product> products;


}
