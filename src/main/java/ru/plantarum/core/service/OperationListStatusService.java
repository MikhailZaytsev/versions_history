package ru.plantarum.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.plantarum.core.entity.OperationList;
import ru.plantarum.core.entity.OperationListStatus;
import ru.plantarum.core.repository.OperationListStatusRepository;
import ru.plantarum.core.web.paging.Direction;
import ru.plantarum.core.web.paging.Order;
import ru.plantarum.core.web.paging.PagingRequest;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OperationListStatusService {

    private final OperationListStatusRepository operationListStatusRepository;

    public List<OperationListStatus> findAllActive() {
        return operationListStatusRepository.findByInactiveFalse();
    }

    public OperationListStatus save(OperationListStatus operationListStatus) {
        return operationListStatusRepository.save(operationListStatus);
    }

    public ru.plantarum.core.web.paging.Page<OperationListStatus> findAll(PagingRequest pagingRequest) {
        int pageNumber = pagingRequest.getStart() / pagingRequest.getLength();
        Order order = pagingRequest.getOrder().stream()
                .findFirst()
                .orElse(new Order(0, Direction.desc));
        String colToOrder = pagingRequest.getColumns().get(order.getColumn()).getData();
        final PageRequest pageRequest = PageRequest.of(pageNumber, pagingRequest.getLength(),
                Sort.Direction.fromString(order.getDir().name()), colToOrder);
        final Page<OperationListStatus> operationListStatuses = operationListStatusRepository.findAll(pageRequest);
        ru.plantarum.core.web.paging.Page<OperationListStatus> page = new ru.plantarum.core.web.paging.Page<>(operationListStatuses);
        page.setDraw(pagingRequest.getDraw());
        return page;
    }

    public boolean editOperationListStatus(Long id, OperationListStatus newOperationListStatus) {
        OperationListStatus oldOperationListStatus = operationListStatusRepository.getOne(id);
        newOperationListStatus.setIdOperationListStatus(id);
        if (!oldOperationListStatus.equals(newOperationListStatus)) {
            operationListStatusRepository.save(newOperationListStatus);
            return true;
        }
        return false;
    }

    public Optional<OperationListStatus> getOne(Long id) {
        return Optional.of(operationListStatusRepository.getOne(id));
    }

    public boolean exists (Long id) {
        return operationListStatusRepository.existsById(id);
    }
}
