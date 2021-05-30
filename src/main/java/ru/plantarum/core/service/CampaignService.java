package ru.plantarum.core.service;

import jdk.nashorn.internal.ir.Optimistic;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ru.plantarum.core.entity.Campaign;
import ru.plantarum.core.entity.Product;
import ru.plantarum.core.repository.CampaignRepository;
import ru.plantarum.core.web.paging.Direction;
import ru.plantarum.core.web.paging.Order;
import ru.plantarum.core.web.paging.PagingRequest;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CampaignService {

    private final CampaignRepository campaignRepository;

    public List<Campaign> findAll() {
        return campaignRepository.findByInactiveIsNull();
    }

    private Page<Campaign> findByContent(@Nullable String content, Pageable pageable) {
        return StringUtils.isBlank(content) ? campaignRepository.findAll(pageable) :
                campaignRepository.findByCampaignNameContainingIgnoreCase(content, pageable);
    }

    public Campaign save(Campaign campaign) {
        return campaignRepository.save(campaign);
    }

    public ru.plantarum.core.web.paging.Page<Campaign> findAll(PagingRequest pagingRequest) {

        String stringToFind = pagingRequest.getColumns().get(1).getSearch().getValue();

        int pageNumber = pagingRequest.getStart() / pagingRequest.getLength();
        Order order = pagingRequest.getOrder().stream()
                .findFirst()
                .orElse(new Order(0, Direction.desc));
        String colToOrder = pagingRequest.getColumns().get(order.getColumn()).getData();
        final PageRequest pageRequest = PageRequest.of(pageNumber, pagingRequest.getLength(), Sort.Direction.fromString(
                order.getDir().name()), colToOrder);
        final Page<Campaign> filteredCampaigns = findByContent(stringToFind, pageRequest);
        ru.plantarum.core.web.paging.Page<Campaign> page = new ru.plantarum.core.web.paging.Page(filteredCampaigns);
        page.setDraw(pagingRequest.getDraw());
        return page;
    }

    public Optional<Campaign> getOne(Long id) {
        return Optional.of(campaignRepository.getOne(id));
    }

    public boolean exists(Long id) {
        return campaignRepository.existsById(id);
    }

    public Campaign findByName(String name) {
        return campaignRepository.findByCampaignNameIgnoreCase(name);
    }

    public Campaign deleteCampaign(Long id) {
        Campaign campaign = campaignRepository.getOne(id);
        if (campaign.getInactive() == null) {
            campaign.setInactive(OffsetDateTime.now());
        }
        return campaign;
    }
}
