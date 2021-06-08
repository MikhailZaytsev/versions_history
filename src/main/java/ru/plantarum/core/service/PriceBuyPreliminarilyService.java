package ru.plantarum.core.service;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.plantarum.core.entity.PriceBuyPreliminarily;
import ru.plantarum.core.repository.PriceBuyPreliminarilyRepository;
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
public class PriceBuyPreliminarilyService {

    private final PriceBuyPreliminarilyRepository priceBuyPreliminarilyRepository;
    private final CriteriaUtils criteriaUtils;

    public PriceBuyPreliminarily save(PriceBuyPreliminarily priceBuyPreliminarily) {
        return priceBuyPreliminarilyRepository.save(priceBuyPreliminarily);
    }

    public boolean exists(Long id) {
        return priceBuyPreliminarilyRepository.existsById(id);
    }

    public Optional<PriceBuyPreliminarily> getOne(Long id) {
        return Optional.of(priceBuyPreliminarilyRepository.getOne(id));
    }

    public ru.plantarum.core.web.paging.Page<PriceBuyPreliminarily> findAll(PagingRequest pagingRequest) {

        final List<SearchCriteria> criteriaList = pagingRequest.getColumns()
                .stream().filter(c -> !(c.getSearch().getValue().isEmpty()))
                .map(column -> new SearchCriteria(column.getData(),
                        SearchCriteria.OPERATION_EQUALS, column.getSearch().getValue())
                ).collect(Collectors.toList());

        final Predicate predicates = criteriaUtils.getPredicate(criteriaList,
                PriceBuyPreliminarily.class, "priceBuyPreliminarily");

        int pageNumber = pagingRequest.getStart() / pagingRequest.getLength();
        Order order = pagingRequest.getOrder().stream()
                .findFirst()
                .orElse(new Order(0, Direction.desc));
        String colToOrder = pagingRequest.getColumns().get(order.getColumn()).getData();
        final PageRequest pageRequest = PageRequest.of(pageNumber, pagingRequest.getLength(), Sort.Direction.fromString(
                order.getDir().name()), colToOrder);
        ru.plantarum.core.web.paging.Page<PriceBuyPreliminarily> page = new ru.plantarum.core.web.paging.Page(
                priceBuyPreliminarilyRepository.findAll(predicates, pageRequest));
        page.setDraw(pagingRequest.getDraw());
        return page;
    }
}
