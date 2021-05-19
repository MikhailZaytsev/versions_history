package ru.plantarum.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.plantarum.core.entity.OperationRow;

@Repository
public interface OperationRowRepository extends JpaRepository<OperationRow, Long> {
}
