package ru.plantarum.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.plantarum.core.entity.*;
import ru.plantarum.core.repository.CounterAgentRepository;
import ru.plantarum.core.repository.ProductRepository;
import ru.plantarum.core.utils.search.CriteriaUtils;
import ru.plantarum.core.web.paging.Column;
import ru.plantarum.core.web.paging.Page;
import ru.plantarum.core.web.paging.PagingRequest;
import ru.plantarum.core.web.paging.Search;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;

class CounterAgentServiceTest {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    private final CounterAgentRepository repository = Mockito.mock(CounterAgentRepository.class);
    private final CriteriaUtils criteriaUtils = Mockito.mock(CriteriaUtils.class);

    private final CounterAgentService counterAgentService =
            new CounterAgentService(repository, criteriaUtils);

    CounterAgentType getCounterAgentType() {
        CounterAgentType counterAgentType = CounterAgentType.builder()
                .idCounterAgentType(55L)
                .counterAgentTypeName("Тип")
                .counterAgentTypeComment("что-то")
                .build();
        return counterAgentType;
    }

    List<CounterAgent> createCounterAgents() {
        final CounterAgent counterAgent1 = CounterAgent.builder()
                .idCounterAgent(20L)
                .counterAgentName("bobr1")
                .counterAgentPhone("нет")
                .counterAgentProfile("нет")
                .counterAgentType(getCounterAgentType())
                .build();

        final CounterAgent counterAgent2 = CounterAgent.builder()
                .idCounterAgent(20L)
                .counterAgentName("bobr2")
                .counterAgentPhone("нет")
                .counterAgentProfile("нет")
                .counterAgentType(getCounterAgentType())
                .build();

        final CounterAgent counterAgent3 = CounterAgent.builder()
                .idCounterAgent(20L)
                .counterAgentName("vidr3")
                .counterAgentPhone("нет")
                .counterAgentProfile("нет")
                .counterAgentType(getCounterAgentType())
                .build();

        List<CounterAgent> counterAgents = new ArrayList<CounterAgent>();
        counterAgents.add(counterAgent1);
        counterAgents.add(counterAgent2);
        counterAgents.add(counterAgent3);

        return counterAgents;
    }

    CounterAgent createCounterAgent() {
        final CounterAgent counterAgent = CounterAgent.builder()
                .idCounterAgent(20L)
                .counterAgentName("test")
                .counterAgentPhone("нет")
                .counterAgentProfile("нет")
                .counterAgentType(getCounterAgentType())
                .build();


        return counterAgent;
    }

    PagingRequest createRequest() throws JsonProcessingException {
        String json = "{\"draw\":21,\"columns\":[{\"data\":\"idCounterAgent\",\"name\":\"\",\"searchable\":true,\"orderable\":true,\"search\":{\"value\":\"\",\"regexp\":false}},{\"data\":\"counterAgentName\",\"name\":\"\",\"searchable\":true,\"orderable\":true,\"search\":{\"value\":\"\",\"regexp\":false}},{\"data\":\"counterAgentProfile\",\"name\":\"\",\"searchable\":true,\"orderable\":true,\"search\":{\"value\":\"\",\"regexp\":false}},{\"data\":\"counterAgentPhone\",\"name\":\"\",\"searchable\":true,\"orderable\":true,\"search\":{\"value\":\"\",\"regexp\":false}},{\"data\":\"counterAgentType.counterAgentTypeName\",\"name\":\"\",\"searchable\":true,\"orderable\":true,\"search\":{\"value\":\"wg\",\"regexp\":false}},{\"data\":\"inactive\",\"name\":\"\",\"searchable\":true,\"orderable\":true,\"search\":{\"value\":\"\",\"regexp\":false}}],\"order\":[{\"column\":0,\"dir\":\"desc\"}],\"start\":0,\"length\":10,\"search\":{\"value\":\"\",\"regexp\":false}}";

        PagingRequest pagingRequest = objectMapper.readValue(json,PagingRequest.class);
        return pagingRequest;
    }


    @Test
    void delete_counter_agent_if_inactive_is_null() {

        final CounterAgent counterAgent = CounterAgent.builder()
                .idCounterAgent(23L)
                .build();

        Mockito.when(repository.getOne(any())).thenReturn(counterAgent);

        counterAgentService.deleteCounterAgent(23L);
        Assertions.assertThat(counterAgent.getInactive()).isNotNull();

    }

    @Test
    void delete_counter_agent_if_inactive_is_not_null() {
        final OffsetDateTime now = OffsetDateTime.now();

        final CounterAgent counterAgent = CounterAgent.builder().
                idCounterAgent(23L)
                .inactive(now)
                .build();

        Mockito.when(repository.getOne(any())).thenReturn(counterAgent);

        counterAgentService.deleteCounterAgent(23L);

        Assertions.assertThat((counterAgent.getInactive()).isEqual(now));
    }

    @Test
    void find_all_if_string_to_find_is_null() throws JsonProcessingException {

        List<CounterAgent> counterAgents = createCounterAgents();

        org.springframework.data.domain.Page<CounterAgent> pagedResponse = new PageImpl<>(counterAgents);

        PagingRequest pagingRequest = createRequest();
        pagingRequest.getColumns().get(1).getSearch().setValue(null);

        //Mockito.when(repository.findAll()).thenReturn(pagedResponse);
        Mockito.when(repository.findAll(any(Pageable.class))).thenReturn(pagedResponse);
        Page page = counterAgentService.findAll(pagingRequest);
        Assertions.assertThat(page.getData().equals(counterAgents));
    }

    @Test
    void find_all_if_string_to_find_is_not_null() throws JsonProcessingException {
        List<CounterAgent> counterAgents = createCounterAgents();

        String content = "obr";

        counterAgents.removeIf(product -> !product.getCounterAgentName().contains(content));

        PagingRequest pagingRequest = createRequest();
        pagingRequest.getColumns().get(1).getSearch().setValue(content);

        org.springframework.data.domain.Page<CounterAgent> pagedResponse = new PageImpl<>(counterAgents);

        Mockito.when(repository.findByCounterAgentNameContainingIgnoreCase(any(String.class), any(Pageable.class))).thenReturn(pagedResponse);
        Page page = counterAgentService.findAll(pagingRequest);
        Assertions.assertThat(page.getData().equals(counterAgents));
    }

    @Test
    void edit_counter_agent_if_new_counter_agent_equal() {
        CounterAgent oldCounterAgent = createCounterAgent();
        CounterAgent newCounterAgent = createCounterAgent();

        Mockito.when(repository.getOne(any(Long.class))).thenReturn(oldCounterAgent);
        boolean res = counterAgentService.editCounterAgent(oldCounterAgent.getIdCounterAgent(), newCounterAgent);
        assert !res;
    }

    @Test
    void edit_counter_agent_if_new_counter_agent_not_equal() {
        CounterAgent oldCounterAgent = createCounterAgent();
        CounterAgent newCounterAgent = createCounterAgent();
        newCounterAgent.setCounterAgentPhone("deer");

        Mockito.when(repository.getOne(any(Long.class))).thenReturn(oldCounterAgent);
        Mockito.when(repository.save(any(CounterAgent.class))).thenReturn(newCounterAgent);
        boolean res = counterAgentService.editCounterAgent(oldCounterAgent.getIdCounterAgent(), newCounterAgent);
        assert res;
    }

}
