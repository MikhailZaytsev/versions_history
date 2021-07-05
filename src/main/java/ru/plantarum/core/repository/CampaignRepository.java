package ru.plantarum.core.repository;

import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;
import org.springframework.stereotype.Repository;
import ru.plantarum.core.entity.Campaign;
import ru.plantarum.core.entity.QCampaign;

import java.util.List;

@Repository
public interface CampaignRepository extends QuerydslPredicateExecutor<Campaign>,
        QuerydslBinderCustomizer<QCampaign>, JpaRepository<Campaign, Long> {

    @Override
    default void customize(QuerydslBindings bindings, QCampaign root) {
        bindings.bind(String.class)
                .first((SingleValueBinding<StringPath, String>)
                        StringExpression::containsIgnoreCase);
    }

    Campaign findByCampaignNameIgnoreCase(String name);

    List<Campaign> findByInactiveIsNull();

    boolean existsCampaignByCampaignName(String name);
}
