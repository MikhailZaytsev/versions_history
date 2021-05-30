package ru.plantarum.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.plantarum.core.entity.PriceSale;

@Repository
public interface PriceSaleRepository extends JpaRepository<PriceSale, Long> {

}
