package ru.plantarum.core.service;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.plantarum.core.entity.Campaign;
import ru.plantarum.core.repository.CampaignRepository;
import ru.plantarum.core.utils.search.CriteriaUtils;
import ru.plantarum.core.utils.search.SearchCriteria;
import ru.plantarum.core.web.paging.Direction;
import ru.plantarum.core.web.paging.Order;
import ru.plantarum.core.web.paging.PagingRequest;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final CriteriaUtils criteriaUtils;

    public List<Campaign> findAll() {
        return campaignRepository.findAll();
    }

    public Campaign save(Campaign campaign) {
        return campaignRepository.save(campaign);
    }

    public ru.plantarum.core.web.paging.Page<Campaign> findAll(PagingRequest pagingRequest) {

        final List<SearchCriteria> criteriaList = pagingRequest.getColumns()
                .stream().filter(c -> !(c.getSearch().getValue().isEmpty()))
                .map(column -> new SearchCriteria(column.getData(),
                        SearchCriteria.OPERATION_EQUALS, column.getSearch().getValue())
                ).collect(Collectors.toList());

        final Predicate predicates = criteriaUtils.getPredicate(criteriaList,
                Campaign.class, "campaign");

        int pageNumber = pagingRequest.getStart() / pagingRequest.getLength();
        Order order = pagingRequest.getOrder().stream()
                .findFirst()
                .orElse(new Order(0, Direction.desc));
        String colToOrder = pagingRequest.getColumns().get(order.getColumn()).getData();
        final PageRequest pageRequest = PageRequest.of(pageNumber, pagingRequest.getLength(), Sort.Direction.fromString(
                order.getDir().name()), colToOrder);
        final Page<Campaign> filteredCampaigns = campaignRepository.findAll(predicates, pageRequest);
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

    public boolean exists(String name) {
        return campaignRepository.existsCampaignByCampaignName(name);
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

    public List<Campaign> findAllActive() {
        return campaignRepository.findByInactiveIsNull();
    }
}
