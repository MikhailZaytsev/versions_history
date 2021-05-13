package ru.plantarum.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.plantarum.core.entity.CounterAgentNote;

import java.util.List;

@Repository
public interface CounterAgentNoteRepository extends JpaRepository<CounterAgentNote, Long> {
    List<CounterAgentNote> findByCounterAgentIdCounterAgent(Long id);
}
