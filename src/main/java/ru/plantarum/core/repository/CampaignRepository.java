package ru.plantarum.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.plantarum.core.entity.Campaign;

import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    Page<Campaign> findByCampaignNameContainingIgnoreCase(String content, Pageable pageable);

    Campaign findByCampaignNameIgnoreCase(String name);

    List<Campaign> findByInactiveIsNull();
}
