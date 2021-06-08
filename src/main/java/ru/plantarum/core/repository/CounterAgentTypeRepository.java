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
import ru.plantarum.core.entity.CounterAgentType;
import ru.plantarum.core.entity.QCounterAgentType;

public interface CounterAgentTypeRepository extends QuerydslPredicateExecutor<CounterAgentType>,
        QuerydslBinderCustomizer<QCounterAgentType>, JpaRepository<CounterAgentType, Long> {

    @Override
    default void customize(QuerydslBindings bindings, QCounterAgentType root) {
        bindings.bind(String.class)
                .first((SingleValueBinding<StringPath, String>)
                        StringExpression::containsIgnoreCase);
    }

    CounterAgentType findByCounterAgentTypeNameIgnoreCase(String counterAgentTypeName);

    Page<CounterAgentType> findByCounterAgentTypeNameContainingIgnoreCase(String content, Pageable pageable);

    boolean existsCounterAgentTypeByCounterAgentTypeNameIgnoreCase(String counterAgentTypeName);
}
