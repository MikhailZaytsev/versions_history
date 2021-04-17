package ru.plantarum.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.plantarum.core.entity.OperationType;

@Repository
public interface OperationTypeRepository extends JpaRepository<OperationType, Long> {

}
