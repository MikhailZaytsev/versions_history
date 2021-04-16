package ru.plantarum.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.plantarum.core.entity.OrganType;

@Repository
public interface OrganTypeRepository extends JpaRepository<OrganType, Long> {
}
