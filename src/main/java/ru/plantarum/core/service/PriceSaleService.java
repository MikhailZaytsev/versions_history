package ru.plantarum.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.plantarum.core.entity.PriceBuyPreliminarily;
import ru.plantarum.core.entity.PriceSale;
import ru.plantarum.core.repository.PriceSaleRepository;
import ru.plantarum.core.web.paging.Direction;
import ru.plantarum.core.web.paging.Order;
import ru.plantarum.core.web.paging.PagingRequest;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PriceSaleService {
    private final PriceSaleRepository priceSaleRepository;

    public PriceSale save(PriceSale priceSale) {
        return priceSaleRepository.save(priceSale);
    }

    public boolean exists(Long id) {
        return priceSaleRepository.existsById(id);
    }

    public Optional<PriceSale> getOne(Long id) {
        return Optional.of(priceSaleRepository.getOne(id));
    }

    public ru.plantarum.core.web.paging.Page<PriceSale> findAll(PagingRequest pagingRequest) {
        int pageNumber = pagingRequest.getStart() / pagingRequest.getLength();
        Order order = pagingRequest.getOrder().stream()
                .findFirst()
                .orElse(new Order(0, Direction.desc));
        String colToOrder = pagingRequest.getColumns().get(order.getColumn()).getData();
        final PageRequest pageRequest = PageRequest.of(pageNumber, pagingRequest.getLength(), Sort.Direction.fromString(
                order.getDir().name()), colToOrder);
        ru.plantarum.core.web.paging.Page<PriceSale> page = new ru.plantarum.core.web.paging.Page(
                priceSaleRepository.findAll(pageRequest));
        page.setDraw(pagingRequest.getDraw());
        return page;
    }
}
