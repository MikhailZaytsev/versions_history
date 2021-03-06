package ru.plantarum.core.repository;

import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;
import org.springframework.stereotype.Repository;
import ru.plantarum.core.entity.OperationListStatus;
import ru.plantarum.core.entity.QOperationListStatus;

import java.util.List;

@Repository
public interface OperationListStatusRepository extends QuerydslPredicateExecutor<OperationListStatus>,
        QuerydslBinderCustomizer<QOperationListStatus>, JpaRepository<OperationListStatus, Long> {
    List<OperationListStatus> findByInactiveFalse();

    @Override
    default void customize(QuerydslBindings bindings, QOperationListStatus root) {
        bindings.bind(String.class)
                .first((SingleValueBinding<StringPath, String>)
                        StringExpression::containsIgnoreCase);
    }

}
