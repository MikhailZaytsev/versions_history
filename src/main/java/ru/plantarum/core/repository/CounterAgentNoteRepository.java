package ru.plantarum.core.repository;

import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;
import org.springframework.stereotype.Repository;
import ru.plantarum.core.entity.CounterAgentNote;
import ru.plantarum.core.entity.QCounterAgent;

import java.util.List;

@Repository
public interface CounterAgentNoteRepository extends QuerydslPredicateExecutor<CounterAgentNote>,
        QuerydslBinderCustomizer<QCounterAgent>, JpaRepository<CounterAgentNote, Long> {

    @Override
    default void customize(QuerydslBindings bindings, QCounterAgent root) {
        bindings.bind(String.class)
        .first((SingleValueBinding<StringPath, String>)
                StringExpression::containsIgnoreCase);
    }

    List<CounterAgentNote> findByCounterAgentIdCounterAgent(Long id);
}
