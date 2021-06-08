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
import ru.plantarum.core.entity.OperationType;
import ru.plantarum.core.entity.QOperationType;

@Repository
public interface OperationTypeRepository extends QuerydslPredicateExecutor<OperationType>,
        QuerydslBinderCustomizer<QOperationType>, JpaRepository<OperationType, Long> {

        @Override
        default void customize(QuerydslBindings bindings, QOperationType root) {
                bindings.bind(String.class)
                        .first((SingleValueBinding<StringPath, String>)
                                StringExpression::containsIgnoreCase);
        }

        OperationType findByOperationTypeNameIgnoreCase(String operationTypeName);

        Page<OperationType> findByOperationTypeNameContainingIgnoreCase(String content, Pageable pageable);

        boolean existsOperationTypeByOperationTypeNameIgnoreCase(String operationTypeName);
}
