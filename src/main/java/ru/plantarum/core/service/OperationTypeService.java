package ru.plantarum.core.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ru.plantarum.core.entity.OperationType;
import ru.plantarum.core.repository.OperationTypeRepository;
import ru.plantarum.core.web.paging.Direction;
import ru.plantarum.core.web.paging.Order;
import ru.plantarum.core.web.paging.PagingRequest;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OperationTypeService {

    private final OperationTypeRepository operationTypeRepository;

    public List<OperationType> findAll() {
        return operationTypeRepository.findAll();
    }

    private Page<OperationType> findByContent(@Nullable String content, Pageable pageable) {
        return StringUtils.isBlank(content) ? operationTypeRepository.findAll(pageable) :
                operationTypeRepository.findByOperationTypeNameContainingIgnoreCase(content, pageable);
    }

    public ru.plantarum.core.web.paging.Page<OperationType> findAll(PagingRequest pagingRequest) {

        String stringToFind = pagingRequest.getColumns().get(1).getSearch().getValue();

        int pageNumber = pagingRequest.getStart() / pagingRequest.getLength();
        Order order = pagingRequest.getOrder().stream().findFirst().
                orElse(new Order(0, Direction.desc));
        String colToOrder = pagingRequest.getColumns().get(order.getColumn()).getData();
        final PageRequest pageRequest = PageRequest.of(pageNumber, pagingRequest.getLength(),
                Sort.Direction.fromString(order.getDir().name()), colToOrder);
        final Page<OperationType> filteredOperationTypes = findByContent(stringToFind, pageRequest);
        ru.plantarum.core.web.paging.Page<OperationType> page = new ru.plantarum.core.web.paging.Page<>(filteredOperationTypes);
        page.setDraw(pagingRequest.getDraw());
        return page;
    }

    public boolean editOperationType(Long id, OperationType newOperationType) {
        OperationType operationType = operationTypeRepository.getOne(id);
        newOperationType.setIdOperationType(id);
        if (!operationType.equals(newOperationType)) {
            if (operationType.getOperationTypeName().equalsIgnoreCase(newOperationType.getOperationTypeName())) {
                operationTypeRepository.save(newOperationType);
                return true;
            } else if (!exists(newOperationType.getOperationTypeName())) {
                operationTypeRepository.save(newOperationType);
                return true;
            }
        }
        return false;
    }

    public Optional<OperationType> getOne(Long id) {
        return Optional.of(operationTypeRepository.getOne(id));
    }

    public boolean exists(String name) {
        return operationTypeRepository.existsOperationTypeByOperationTypeNameIgnoreCase(name);
    }

    public boolean exists(Long id) {
        return operationTypeRepository.existsById(id);
    }

    public OperationType save(OperationType operationType) {
        return operationTypeRepository.save(operationType);
    }

    public OperationType findByOperationTypeName(String operationTypeName) {
        return operationTypeRepository.findByOperationTypeNameIgnoreCase(operationTypeName);
    }
}
