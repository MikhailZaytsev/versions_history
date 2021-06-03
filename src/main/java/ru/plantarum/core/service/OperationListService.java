package ru.plantarum.core.service;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.plantarum.core.entity.OperationList;
import ru.plantarum.core.entity.TradeMark;
import ru.plantarum.core.repository.OperationListRepository;
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
public class OperationListService {

    private final OperationListRepository operationListRepository;
    private final CriteriaUtils criteriaUtils;

    public Optional<OperationList> getOne(Long id) {
        return operationListRepository.findById(id);
    }

    public OperationList save(OperationList operationList) {
        return operationListRepository.save(operationList);
    }

    public ru.plantarum.core.web.paging.Page<OperationList> findAll(PagingRequest pagingRequest) {

        final List<SearchCriteria> criteriaList = pagingRequest.getColumns()
                .stream().filter(c -> !(c.getSearch().getValue().isEmpty()))
                .map(column -> new SearchCriteria(column.getData(),
                        SearchCriteria.OPERATION_EQUALS, column.getSearch().getValue())
                ).collect(Collectors.toList());

        final Predicate predicates = criteriaUtils.getPredicate(criteriaList,
                OperationList.class, "operationList");

        int pageNumber = pagingRequest.getStart() / pagingRequest.getLength();
        Order order = pagingRequest.getOrder().stream()
                .findFirst()
                .orElse(new Order(0, Direction.desc));
        String colToOrder = pagingRequest.getColumns().get(order.getColumn()).getData();
        final PageRequest pageRequest = PageRequest.of(pageNumber, pagingRequest.getLength(), Sort.Direction.fromString(
                order.getDir().name()), colToOrder);
        final Page<OperationList> operationLists = operationListRepository.findAll(predicates, pageRequest);
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