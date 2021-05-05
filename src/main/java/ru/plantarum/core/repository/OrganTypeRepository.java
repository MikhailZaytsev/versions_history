package ru.plantarum.core.repository;

import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.plantarum.core.entity.OrganType;

@Repository
public interface OrganTypeRepository extends JpaRepository<OrganType, Long> {

    OrganType findByOrganTypeName(String organTypeName);

    Page<OrganType> findByOrganTypeNameContainingIgnoreCase(String content, Pageable pageable);
}
