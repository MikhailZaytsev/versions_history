package ru.plantarum.core.security.repository;

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
import ru.plantarum.core.security.entity.Person;
import ru.plantarum.core.security.entity.QPerson;

@Repository
public interface PersonRepository extends QuerydslPredicateExecutor<Person>,
        QuerydslBinderCustomizer<QPerson>, JpaRepository<Person, Long> {

    @Override
    default void customize(QuerydslBindings bindings, QPerson qPerson) {
        bindings.bind(String.class)
                .first((SingleValueBinding<StringPath, String>)
                        StringExpression::containsIgnoreCase);
    }

    Person findByPersonName(String name);
}
