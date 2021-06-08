package ru.plantarum.core.utils.search;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PredicatesBuilder<T> {
    private final List<SearchCriteria> params;

    public PredicatesBuilder(List<SearchCriteria> params) {
        this.params = params;
    }

    public BooleanExpression build(PathBuilder<T> pathBuilder, Class<T> type) {
        List<BooleanExpression> predicates = params.stream().map(param -> {
            QueryDslPredicate<T> predicate = new QueryDslPredicate<T>(param, pathBuilder, type);
            return predicate.getPredicate();
        }).filter(Objects::nonNull).collect(Collectors.toList());

        BooleanExpression result = Expressions.asBoolean(true).isTrue();
        for (BooleanExpression predicate : predicates) {
            result = result.and(predicate);
        }
        return result;
    }

}
