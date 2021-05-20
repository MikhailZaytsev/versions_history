package ru.plantarum.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.plantarum.core.entity.OperationList;
import ru.plantarum.core.entity.Product;
import ru.plantarum.core.repository.OperationListRepository;
import ru.plantarum.core.web.paging.Direction;
import ru.plantarum.core.web.paging.Order;
import ru.plantarum.core.web.paging.PagingRequest;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OperationListService {

    private final OperationListRepository operationListRepository;

    public List<OperationList> allLists() {
        return operationListRepository.findAll();
    }

    public Optional<OperationList> getOne(Long id) {
        return Optional.of(operationListRepository.getOne(id));
    }

    public OperationList save(OperationList operationList) {
        return operationListRepository.save(operationList);
    }

    public ru.plantarum.core.web.paging.Page<OperationList> findAll(PagingRequest pagingRequest) {
        int pageNumber = pagingRequest.getStart() / pagingRequest.getLength();
        Order order = pagingRequest.getOrder().stream()
                .findFirst()
                .orElse(new Order(0, Direction.desc));
        String colToOrder = pagingRequest.getColumns().get(order.getColumn()).getData();
        final PageRequest pageRequest = PageRequest.of(pageNumber, pagingRequest.getLength(), Sort.Direction.fromString(
                order.getDir().name()), colToOrder);
        final Page<OperationList> operationLists = operationListRepository.findAll(pageRequest);
        ru.plantarum.core.web.paging.Page<OperationList> page = new ru.plantarum.core.web.paging.Page(operationLists);
        page.setDraw(pagingRequest.getDraw());
        return page;
    }

    public boolean edit(Long id, OperationList newOperationList) {
        OperationList oldOperationList = operationListRepository.getOne(id);
        newOperationList.setIdOperationList(id);
        if (!oldOperationList.equals(newOperationList)) {
            operationListRepository.save(newOperationList);
            return true;
        } else {
            return false;
        }
    }

    public boolean exists(Long id) {
        return operationListRepository.existsById(id);
    }
}