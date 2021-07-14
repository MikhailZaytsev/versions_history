package ru.plantarum.core.service;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.plantarum.core.entity.PriceSale;
import ru.plantarum.core.repository.PriceSaleRepository;
import ru.plantarum.core.utils.search.CriteriaUtils;
import ru.plantarum.core.utils.search.SearchCriteria;
import ru.plantarum.core.web.paging.Direction;
import ru.plantarum.core.web.paging.Order;
import ru.plantarum.core.web.paging.PagingRequest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PriceSaleService {
    private final PriceSaleRepository priceSaleRepository;
    private final CriteriaUtils criteriaUtils;

    public PriceSale save(PriceSale priceSale) {
        return priceSaleRepository.save(priceSale);
    }

    public boolean exists(Long id) {
        return priceSaleRepository.existsById(id);
    }

    public boolean exists(Long idProduct, String propertyPrice, Long idCampaign) {
        return priceSaleRepository.existsPriceSaleByProduct_IdProductAndPropertyPriceAndCampaign_IdCampaign(idProduct, propertyPrice, idCampaign);
    }

    public Optional<PriceSale> getOne(Long id) {
        return Optional.of(priceSaleRepository.getOne(id));
    }

    public List<PriceSale> saveAll (List<PriceSale> priceSaleList) {
        return priceSaleRepository.saveAll(priceSaleList);
    }

    public ru.plantarum.core.web.paging.Page<PriceSale> findAll(PagingRequest pagingRequest) {

        final List<SearchCriteria> criteriaList = pagingRequest.getColumns()
                .stream().filter(c -> !(c.getSearch().getValue().isEmpty()))
                .map(column -> new SearchCriteria(column.getData(),
                        SearchCriteria.OPERATION_EQUALS, column.getSearch().getValue())
                ).collect(Collectors.toList());

        final Predicate predicates = criteriaUtils.getPredicate(criteriaList,
                PriceSale.class, "priceSale");

        int pageNumber = pagingRequest.getStart() / pagingRequest.getLength();
        Order order = pagingRequest.getOrder().stream()
                .findFirst()
                .orElse(new Order(0, Direction.desc));
        String colToOrder = pagingRequest.getColumns().get(order.getColumn()).getData();
        final PageRequest pageRequest = PageRequest.of(pageNumber, pagingRequest.getLength(), Sort.Direction.fromString(
                order.getDir().name()), colToOrder);
        ru.plantarum.core.web.paging.Page<PriceSale> page = new ru.plantarum.core.web.paging.Page(
                priceSaleRepository.findAll(predicates, pageRequest));
        page.setDraw(pagingRequest.getDraw());
        return page;
    }
}
