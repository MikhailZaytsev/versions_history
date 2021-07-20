package ru.plantarum.core.service;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.plantarum.core.entity.TradeMark;
import ru.plantarum.core.repository.TradeMarkRepository;
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
public class TradeMarkService {

    private final TradeMarkRepository tradeMarkRepository;
    private final CriteriaUtils criteriaUtils;

    public List<TradeMark> findAllActive() {
        return tradeMarkRepository.findByInactiveIsNull();
    }

    public TradeMark deleteTradeMark(Long id) {
        TradeMark tradeMark = tradeMarkRepository.getOne(id);
        if (tradeMark.getInactive() == null) {
            tradeMark.setInactive(OffsetDateTime.now());
        }
        return tradeMark;
    }

    public ru.plantarum.core.web.paging.Page<TradeMark> findAll(PagingRequest pagingRequest) {

        final List<SearchCriteria> criteriaList = pagingRequest.getColumns()
                .stream().filter(c -> !(c.getSearch().getValue().isEmpty()))
                .map(column -> new SearchCriteria(column.getData(),
                        SearchCriteria.OPERATION_EQUALS, column.getSearch().getValue())
                ).collect(Collectors.toList());

        final Predicate predicates = criteriaUtils.getPredicate(criteriaList,
                TradeMark.class, "tradeMark");

        int pageNumber = pagingRequest.getStart() / pagingRequest.getLength();
        Order order = pagingRequest.getOrder().stream()
                .findFirst()
                .orElse(new Order(0, Direction.desc));
        String colToOrder = pagingRequest.getColumns().get(order.getColumn()).getData();
        final PageRequest pageRequest = PageRequest.of(pageNumber, pagingRequest.getLength(),
                Sort.Direction.fromString(order.getDir().name()), colToOrder);
        final Page<TradeMark> filteredTradeMarks = tradeMarkRepository.findAll(predicates, pageRequest);
        ru.plantarum.core.web.paging.Page<TradeMark> page = new ru.plantarum.core.web.paging.Page(filteredTradeMarks);
        page.setDraw(pagingRequest.getDraw());
        return page;
    }

    public boolean editTradeMark(Long id, TradeMark newTradeMark) {
        TradeMark tradeMark = tradeMarkRepository.getOne(id);
        newTradeMark.setIdTradeMark(id);
        if (!tradeMark.equals(newTradeMark)) {
            if (tradeMark.getTradeMarkName().equalsIgnoreCase(newTradeMark.getTradeMarkName())) {
                tradeMarkRepository.save(newTradeMark);
                return true;
            } else if (!exists(newTradeMark.getTradeMarkName())) {
                tradeMarkRepository.save(newTradeMark);
                return true;
            }
        }
        return false;
    }

    public Optional<TradeMark> getOne(Long id) {
        return Optional.of(tradeMarkRepository.getOne(id));
    }

    public TradeMark findById(Long id) {
        return tradeMarkRepository.getOne(id);
    }

    public boolean exists(String name) {
        return tradeMarkRepository.existsTradeMarkByTradeMarkNameIgnoreCase(name);
    }

    public boolean exists(Long id) {
        return tradeMarkRepository.existsById(id);
    }

    public TradeMark save(TradeMark tradeMark) {
        return tradeMarkRepository.save(tradeMark);
    }

    public TradeMark findByTradeMarkName(String tradeMarkName) {
        return tradeMarkRepository.findByTradeMarkNameIgnoreCase(tradeMarkName);
    }

}
