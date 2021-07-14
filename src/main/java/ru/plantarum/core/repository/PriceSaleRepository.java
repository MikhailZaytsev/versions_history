package ru.plantarum.core.repository;

import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;
import org.springframework.stereotype.Repository;
import ru.plantarum.core.entity.PriceSale;
import ru.plantarum.core.entity.QPriceSale;

@Repository
public interface PriceSaleRepository extends QuerydslPredicateExecutor<PriceSale>,
        QuerydslBinderCustomizer<QPriceSale>, JpaRepository<PriceSale, Long> {

    @Override
    default void customize(QuerydslBindings bindings, QPriceSale root) {
        bindings.bind(String.class)
                .first((SingleValueBinding<StringPath, String>)
                        StringExpression::containsIgnoreCase);
    }

    boolean existsPriceSaleByProduct_IdProductAndPropertyPriceAndCampaign_IdCampaign(Long idProduct, String propertyPrice, Long idCampaign);
}
