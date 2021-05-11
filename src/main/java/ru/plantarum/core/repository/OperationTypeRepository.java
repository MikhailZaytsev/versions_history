package ru.plantarum.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.plantarum.core.entity.OperationType;

@Repository
public interface OperationTypeRepository extends JpaRepository<OperationType, Long> {
        OperationType findByOperationTypeNameIgnoreCase(String operationTypeName);

        Page<OperationType> findByOperationTypeNameContainingIgnoreCase(String content, Pageable pageable);

        boolean existsOperationTypeByOperationTypeNameIgnoreCase(String operationTypeName);
}
