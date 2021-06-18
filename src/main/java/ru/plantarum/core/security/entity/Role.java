package ru.plantarum.core.security.entity;


import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
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

    private String roleName;

    @Transient
    @ManyToMany(mappedBy = "roles")
    private Set<Person> people;

    @Override
    public String getAuthority() {
        return getRoleName();
    }
}
