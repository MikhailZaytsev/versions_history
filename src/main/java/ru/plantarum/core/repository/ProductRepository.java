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
import ru.plantarum.core.entity.Product;
import ru.plantarum.core.entity.QProduct;

import java.util.List;


@Repository
public interface ProductRepository  extends QuerydslPredicateExecutor<Product>,
        QuerydslBinderCustomizer<QProduct>, JpaRepository<Product, Long>{

        @Override
        default void customize(QuerydslBindings bindings, QProduct root) {
                bindings.bind(String.class)
                        .first((SingleValueBinding<StringPath, String>)
                                StringExpression::containsIgnoreCase);
        }


        List<Product> findByProductNameIgnoreCase(String productName);

        Product findByProductNameAndTradeMark_IdTradeMarkAndNumberInPack(String name, Long id, short numberInPack);

        Page<Product> findByProductNameContainingIgnoreCase(String content, Pageable pageable);

        boolean existsProductByProductNameAndTradeMark_IdTradeMarkAndNumberInPack(String name, Long idTradeMark, short numberInPack);

        boolean existsProductByProductNameIgnoreCase(String productName);

}