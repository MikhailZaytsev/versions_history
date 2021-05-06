package ru.plantarum.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.plantarum.core.entity.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

        Product findByProductNameIgnoreCase(String productName);

        Page<Product> findByProductNameContainingIgnoreCase(String content, Pageable pageable);

        boolean existsProductByProductNameIgnoreCase(String productName);

}