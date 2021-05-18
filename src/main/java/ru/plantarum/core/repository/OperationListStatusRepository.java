package ru.plantarum.core.repository;

import org.hibernate.boot.archive.internal.JarProtocolArchiveDescriptor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.plantarum.core.entity.OperationListStatus;

import java.util.List;

@Repository
public interface OperationListStatusRepository extends JpaRepository<OperationListStatus, Long> {
    List<OperationListStatus> findByInactiveFalse();

    boolean existsOperationListStatusByOperationListStatusNameIgnoreCase(String operationListStatusName);
}
