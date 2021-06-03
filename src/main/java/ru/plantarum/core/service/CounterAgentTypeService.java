package ru.plantarum.core.service;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ru.plantarum.core.entity.CounterAgent;
import ru.plantarum.core.entity.CounterAgentType;
import ru.plantarum.core.entity.TradeMark;
import ru.plantarum.core.repository.CounterAgentTypeRepository;
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
public class CounterAgentTypeService {

    private final CounterAgentTypeRepository counterAgentTypeRepository;
    private final CriteriaUtils criteriaUtils;

    public List<CounterAgentType> findAll() {
        return counterAgentTypeRepository.findAll();
    }

    public ru.plantarum.core.web.paging.Page<CounterAgentType> findAll(PagingRequest pagingRequest) {

        final List<SearchCriteria> criteriaList = pagingRequest.getColumns()
                .stream().filter(c -> !(c.getSearch().getValue().isEmpty()))
                .map(column -> new SearchCriteria(column.getData(),
                        SearchCriteria.OPERATION_EQUALS, column.getSearch().getValue())
                ).collect(Collectors.toList());

        final Predicate predicates = criteriaUtils.getPredicate(criteriaList,
                CounterAgentType.class, "counterAgentType");

        int pageNumber = pagingRequest.getStart() / pagingRequest.getLength();
        Order order = pagingRequest.getOrder().stream()
                .findFirst()
                .orElse(new Order(0, Direction.desc));
        String colToOrder = pagingRequest.getColumns().get(order.getColumn()).getData();
        final PageRequest pageRequest = PageRequest.of(pageNumber, pagingRequest.getLength(),
                Sort.Direction.fromString(order.getDir().name()), colToOrder);
        final Page<CounterAgentType> filteredCounterAgentTypes = counterAgentTypeRepository.findAll(predicates, pageRequest);
        ru.plantarum.core.web.paging.Page<CounterAgentType> page = new ru.plantarum.core.web.paging.Page(filteredCounterAgentTypes);
        page.setDraw(pagingRequest.getDraw());
        return page;
    }

    public boolean editCounterAgentType(Long id, CounterAgentType newCounterAgentType) {
        CounterAgentType counterAgentType = counterAgentTypeRepository.getOne(id);
        newCounterAgentType.setIdCounterAgentType(id);
        if (!counterAgentType.equals(newCounterAgentType)) {
            if (counterAgentType.getCounterAgentTypeName().equalsIgnoreCase(newCounterAgentType.getCounterAgentTypeName())) {
                counterAgentTypeRepository.save(newCounterAgentType);
                return true;
            } else if (!exists(newCounterAgentType.getCounterAgentTypeName())) {
                counterAgentTypeRepository.save(newCounterAgentType);
                return true;
            }
        }
        return false;
    }

    public Optional<CounterAgentType> getOne(Long id) {
        return Optional.of(counterAgentTypeRepository.getOne(id));
    }

    public boolean exists(String name) {
        return counterAgentTypeRepository.existsCounterAgentTypeByCounterAgentTypeNameIgnoreCase(name);
    }

    public boolean exists(Long id) {
        return counterAgentTypeRepository.existsById(id);
    }

    public CounterAgentType save(CounterAgentType counterAgentType) {
        return counterAgentTypeRepository.save(counterAgentType);
    }

    public CounterAgentType findByCounterAgentTypeName(String organTypeName) {
        return counterAgentTypeRepository.findByCounterAgentTypeNameIgnoreCase(organTypeName);
    }
}
