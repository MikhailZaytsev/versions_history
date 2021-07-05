package ru.plantarum.core.service;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.plantarum.core.entity.CounterAgent;
import ru.plantarum.core.repository.CounterAgentRepository;
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
public class CounterAgentService {

    private final CounterAgentRepository counterAgentRepository;
    private final CriteriaUtils criteriaUtils;

    public List<CounterAgent> findAll() {
        return counterAgentRepository.findAll();
    }

    public List<CounterAgent> findAllActive() {
        return counterAgentRepository.findByInactiveIsNull();
    }

    public boolean editCounterAgent(Long id, CounterAgent newCounterAgent) throws DataIntegrityViolationException {
        CounterAgent counterAgent = counterAgentRepository.getOne(id);
        newCounterAgent.setIdCounterAgent(id);
        if (!counterAgent.equals(newCounterAgent)) {
            save(newCounterAgent);
            return true;
//            if (counterAgent.getCounterAgentName().equalsIgnoreCase(newCounterAgent.getCounterAgentPhone())) {
//                counterAgentRepository.save(newCounterAgent);
//                return true;
//            } else if (!exists(newCounterAgent.getCounterAgentName())) {
//                counterAgentRepository.save(newCounterAgent);
//                return true;
//            }
        }
        return false;
    }

    public Optional<CounterAgent> getOne(Long id) {
        return Optional.of(counterAgentRepository.getOne(id));
    }

//    public CounterAgent getOne(Long id) {
//        return counterAgentRepository.getOne(id);
//    }

    public boolean exists(String name) {
        return counterAgentRepository.existsCounterAgentByCounterAgentNameIgnoreCase(name);
    }

    public boolean exists(Long id) {
        return counterAgentRepository.existsById(id);
    }

    public CounterAgent deleteCounterAgent(Long id) {
        CounterAgent counterAgent = counterAgentRepository.getOne(id);
        if (counterAgent.getInactive() == null) {
            OffsetDateTime dateTime = OffsetDateTime.now();
            counterAgent.setInactive(dateTime);
        }
        return counterAgent;
    }

    public CounterAgent save(CounterAgent counterAgent) throws DataIntegrityViolationException {
        return counterAgentRepository.save(counterAgent);
    }

    public CounterAgent findByCounterAgentName(String counterAgentName) {
        return counterAgentRepository.findByCounterAgentNameIgnoreCase(counterAgentName);
    }

    public ru.plantarum.core.web.paging.Page<CounterAgent> findAll(PagingRequest pagingRequest) {

        final List<SearchCriteria> criteriaList = pagingRequest.getColumns()
                .stream().filter(c -> !(c.getSearch().getValue().isEmpty()))
                .map(column -> new SearchCriteria(column.getData(),
                        SearchCriteria.OPERATION_EQUALS, column.getSearch().getValue())
                ).collect(Collectors.toList());

        final Predicate predicates = criteriaUtils.getPredicate(criteriaList,
                CounterAgent.class, "counterAgent");

        int pageNumber = pagingRequest.getStart() / pagingRequest.getLength();
        Order order = pagingRequest.getOrder().stream()
                .findFirst()
                .orElse(new Order(0, Direction.desc));
        String colToOrder = pagingRequest.getColumns().get(order.getColumn()).getData();
        final PageRequest pageRequest = PageRequest.of(pageNumber, pagingRequest.getLength(), Sort.Direction.fromString(
                order.getDir().name()), colToOrder);
        final Page<CounterAgent> filteredCounterAgents = counterAgentRepository.findAll(predicates, pageRequest);
        ru.plantarum.core.web.paging.Page<CounterAgent> page = new ru.plantarum.core.web.paging.Page(filteredCounterAgents);
        page.setDraw(pagingRequest.getDraw());
        return page;
    }

}
