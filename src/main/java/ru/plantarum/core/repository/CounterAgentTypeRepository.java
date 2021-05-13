package ru.plantarum.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.plantarum.core.entity.CounterAgentType;

public interface CounterAgentTypeRepository extends JpaRepository<CounterAgentType, Long> {

    CounterAgentType findByCounterAgentTypeNameIgnoreCase(String counterAgentTypeName);

    Page<CounterAgentType> findByCounterAgentTypeNameContainingIgnoreCase(String content, Pageable pageable);

    boolean existsCounterAgentTypeByCounterAgentTypeNameIgnoreCase(String counterAgentTypeName);
}
