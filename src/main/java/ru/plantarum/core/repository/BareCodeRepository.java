package ru.plantarum.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.plantarum.core.entity.BareCode;

@Repository
public interface BareCodeRepository extends JpaRepository<BareCode, Long> {
}
