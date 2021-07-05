package ru.plantarum.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.plantarum.core.entity.BareCode;
import ru.plantarum.core.entity.Product;

import java.math.BigDecimal;

@Repository
public interface BareCodeRepository extends JpaRepository<BareCode, Long> {

    boolean existsBareCodeByProduct_IdProductAndEan13(Long idProduct, BigDecimal ean13);

}
