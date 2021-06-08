package ru.plantarum.core.repository;

import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;
import org.springframework.stereotype.Repository;
import ru.plantarum.core.entity.CounterAgent;
import ru.plantarum.core.entity.QCounterAgent;

@Repository
public interface CounterAgentRepository extends QuerydslPredicateExecutor<CounterAgent>,
        QuerydslBinderCustomizer<QCounterAgent>, JpaRepository<CounterAgent, Long> {

    @Override
    default void customize(QuerydslBindings bindings, QCounterAgent root) {
        bindings.bind(String.class)
                .first((SingleValueBinding<StringPath, String>)
                        StringExpression::containsIgnoreCase);
    }

    CounterAgent findByCounterAgentNameIgnoreCase(String counterAgentName);

    Page<CounterAgent> findByCounterAgentNameContainingIgnoreCase(String content, Pageable pageable);

    boolean existsCounterAgentByCounterAgentNameIgnoreCase(String counterAgentName);
}
