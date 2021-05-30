package ru.plantarum.core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.Set;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode (exclude = {"priceBuyPreliminarilies", "priceSales"})
@Setter
@Getter
@ToString (exclude = {"priceBuyPreliminarilies", "priceSales"})
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCampaign;

    @NotBlank(message = "Название не должно быть пустым")
    @Size(max = 64)
    @Column(unique = true)
    private String campaignName;

    @Size(max = 255)
    private String campaignComment;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private OffsetDateTime inactive;

    @JsonIgnore
    @OneToMany(mappedBy = "campaign")
    private Set<PriceBuyPreliminarily> priceBuyPreliminarilies;

    @JsonIgnore
    @OneToMany(mappedBy = "campaign")
    private Set<PriceSale> priceSales;
}
