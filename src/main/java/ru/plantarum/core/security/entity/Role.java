package ru.plantarum.core.security.entity;


import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    @NotNull
    @Column(unique = true)
    private String roleName;

    @NotNull
    private String description;

    @Transient
    @ManyToMany(mappedBy = "roles")
    private Set<Person> people;

    @Override
    public String getAuthority() {
        return getRoleName();
    }
}
