package ru.plantarum.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.plantarum.core.entity.OperationList;

@Repository
public interface OperationListRepository extends JpaRepository<OperationList, Long> {
}
