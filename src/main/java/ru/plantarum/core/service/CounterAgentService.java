package ru.plantarum.core.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ru.plantarum.core.entity.CounterAgent;
import ru.plantarum.core.entity.CounterAgentType;
import ru.plantarum.core.entity.Product;
import ru.plantarum.core.repository.CounterAgentRepository;
import ru.plantarum.core.web.paging.Direction;
import ru.plantarum.core.web.paging.Order;
import ru.plantarum.core.web.paging.PagingRequest;

import javax.validation.ConstraintViolationException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CounterAgentService {

    private final CounterAgentRepository counterAgentRepository;

    public Page<CounterAgent> findByContent(String content, Pageable pageable) {
        return StringUtils.isBlank(content) ? counterAgentRepository.findAll(pageable) :
                counterAgentRepository.findByCounterAgentNameContainingIgnoreCase(content, pageable);
    }
    public List<CounterAgent> findAll() {
        return counterAgentRepository.findAll();
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

        String stringToFind = pagingRequest.getColumns().get(1).getSearch().getValue();

        int pageNumber = pagingRequest.getStart() / pagingRequest.getLength();
        Order order = pagingRequest.getOrder().stream()
                .findFirst()
                .orElse(new Order(0, Direction.desc));
        String colToOrder = pagingRequest.getColumns().get(order.getColumn()).getData();
        final PageRequest pageRequest = PageRequest.of(pageNumber, pagingRequest.getLength(), Sort.Direction.fromString(
                order.getDir().name()), colToOrder);
        final Page<CounterAgent> filteredCounterAgents = findByContent(stringToFind, pageRequest);
        ru.plantarum.core.web.paging.Page<CounterAgent> page = new ru.plantarum.core.web.paging.Page(filteredCounterAgents);
        page.setDraw(pagingRequest.getDraw());
        return page;
    }

}
