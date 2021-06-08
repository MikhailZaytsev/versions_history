package ru.plantarum.core.utils.search;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CriteriaUtils {

    public <T> Predicate getPredicate(List<SearchCriteria> criteriaList, Class<T> type, String className ){
        PredicatesBuilder<T> builder = new PredicatesBuilder<T>(criteriaList);
        return builder.build(new PathBuilder<T>(type, className), type);
    }

}
