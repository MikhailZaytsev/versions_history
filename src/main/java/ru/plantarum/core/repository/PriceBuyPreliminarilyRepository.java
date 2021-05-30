package ru.plantarum.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.plantarum.core.entity.PriceBuyPreliminarily;

@Repository
public interface PriceBuyPreliminarilyRepository extends JpaRepository<PriceBuyPreliminarily, Long> {
}
