package ru.plantarum.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;
import ru.plantarum.core.entity.CounterAgent;
import ru.plantarum.core.web.paging.PagingRequest;

@Repository
public interface CounterAgentRepository extends JpaRepository<CounterAgent, Long> {
    CounterAgent findByCounterAgentNameIgnoreCase(String counterAgentName);

    Page<CounterAgent> findByCounterAgentNameContainingIgnoreCase(String content, Pageable pageable);

    boolean existsCounterAgentByCounterAgentNameIgnoreCase(String counterAgentName);
}
