package ru.plantarum.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.plantarum.core.entity.TradeMark;

@Repository
public interface TradeMarkRepository extends JpaRepository<TradeMark, Long> {
}
