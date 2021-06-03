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
import ru.plantarum.core.entity.OrganType;
import ru.plantarum.core.entity.QOrganType;

@Repository
public interface OrganTypeRepository extends QuerydslPredicateExecutor<OrganType>,
        QuerydslBinderCustomizer<QOrganType>, JpaRepository<OrganType, Long> {

    @Override
    default void customize(QuerydslBindings bindings, QOrganType root) {
        bindings.bind(String.class)
                .first((SingleValueBinding<StringPath, String>)
                        StringExpression::containsIgnoreCase);
    }

    OrganType findByOrganTypeNameIgnoreCase(String organTypeName);

    Page<OrganType> findByOrganTypeNameContainingIgnoreCase(String content, Pageable pageable);

    boolean existsOrganTypeByOrganTypeNameIgnoreCase(String organTypeName);
}
