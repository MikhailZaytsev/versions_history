package ru.plantarum.core.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import ru.plantarum.core.entity.TradeMark;

import ru.plantarum.core.repository.TradeMarkRepository;
import ru.plantarum.core.web.paging.Direction;
import ru.plantarum.core.web.paging.Order;
import ru.plantarum.core.web.paging.PagingRequest;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TradeMarkService {

    private final TradeMarkRepository tradeMarkRepository;
    public List<TradeMark> findAll() {
        return tradeMarkRepository.findAll();
    }

    private Page<TradeMark> findByContent(@Nullable String content, Pageable pageable) {
        return StringUtils.isBlank(content) ? tradeMarkRepository.findAll(pageable) :
                tradeMarkRepository.findByTradeMarkNameContainingIgnoreCase(content, pageable);
    }

    public ru.plantarum.core.web.paging.Page<TradeMark> findAll(PagingRequest pagingRequest) {

        String stringToFind = pagingRequest.getColumns().get(1).getSearch().getValue();

        int pageNumber = pagingRequest.getStart() / pagingRequest.getLength();
        Order order = pagingRequest.getOrder().stream()
                .findFirst()
                .orElse(new Order(0, Direction.desc));
        String colToOrder = pagingRequest.getColumns().get(order.getColumn()).getData();
        final PageRequest pageRequest = PageRequest.of(pageNumber, pagingRequest.getLength(),
                Sort.Direction.fromString(order.getDir().name()), colToOrder);
        final Page<TradeMark> filteredTradeMarks = findByContent(stringToFind, pageRequest);
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
            }
            else {
                if (!exists(newTradeMark.getTradeMarkName())) {
                    tradeMarkRepository.save(newTradeMark);
                    return true;
                }
            }
        }
        return false;
    }

    public Optional<TradeMark> getOne(Long id) {
        return Optional.of(tradeMarkRepository.getOne(id));
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
