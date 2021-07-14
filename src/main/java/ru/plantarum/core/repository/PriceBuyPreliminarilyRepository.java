package ru.plantarum.core.repository;

import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;
import org.springframework.stereotype.Repository;
import ru.plantarum.core.entity.PriceBuyPreliminarily;
import ru.plantarum.core.entity.QPriceBuyPreliminarily;

@Repository
public interface PriceBuyPreliminarilyRepository extends QuerydslPredicateExecutor<PriceBuyPreliminarily>,
        QuerydslBinderCustomizer<QPriceBuyPreliminarily>, JpaRepository<PriceBuyPreliminarily, Long> {

    @Override
    default void customize(QuerydslBindings bindings, QPriceBuyPreliminarily root) {
        bindings.bind(String.class)
                .first((SingleValueBinding<StringPath, String>)
                        StringExpression::containsIgnoreCase);
    }

    boolean existsPriceBuyPreliminarilyByProduct_IdProductAndCounterAgent_IdCounterAgentAndCampaign_IdCampaign(Long idProduct, Long idAgent, Long idCampaign);
}
