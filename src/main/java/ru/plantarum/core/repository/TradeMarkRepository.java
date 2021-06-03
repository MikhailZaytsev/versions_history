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
import ru.plantarum.core.entity.QTradeMark;
import ru.plantarum.core.entity.TradeMark;

@Repository
public interface TradeMarkRepository extends QuerydslPredicateExecutor<TradeMark>,
        QuerydslBinderCustomizer<QTradeMark>, JpaRepository<TradeMark, Long> {

    @Override
    default void customize(QuerydslBindings bindings, QTradeMark root) {
        bindings.bind(String.class)
                .first((SingleValueBinding<StringPath, String>)
                        StringExpression::containsIgnoreCase);
    }

    TradeMark findByTradeMarkNameIgnoreCase(String tradeMarkName);

    Page<TradeMark> findByTradeMarkNameContainingIgnoreCase(String content, Pageable pageable);

    boolean existsTradeMarkByTradeMarkNameIgnoreCase(String tradeMarkName);
}
