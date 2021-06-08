package ru.plantarum.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.plantarum.core.entity.CounterAgentType;
import ru.plantarum.core.entity.OrganType;
import ru.plantarum.core.entity.Product;
import ru.plantarum.core.repository.CounterAgentTypeRepository;
import ru.plantarum.core.repository.OrganTypeRepository;
import ru.plantarum.core.repository.ProductRepository;
import ru.plantarum.core.utils.search.CriteriaUtils;
import ru.plantarum.core.web.paging.Page;
import ru.plantarum.core.web.paging.PagingRequest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class CounterAgentTypeServiceTest {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    private final CounterAgentTypeRepository repository = Mockito.mock(CounterAgentTypeRepository.class);
    private final CriteriaUtils criteriaUtils = Mockito.mock(CriteriaUtils.class);

    private final CounterAgentTypeService counterAgentTypeService =
            new CounterAgentTypeService(repository, criteriaUtils);

    List<CounterAgentType> createCounterAgentTypes() {
        final CounterAgentType counterAgentType1 = CounterAgentType.builder()
                .idCounterAgentType(20L)
                .counterAgentTypeName("bobr1")
                .build();

        final CounterAgentType counterAgentType2 = CounterAgentType.builder()
                .idCounterAgentType(30L)
                .counterAgentTypeName("bobr2")
                .build();

        final CounterAgentType counterAgentType3 = CounterAgentType.builder()
                .idCounterAgentType(30L)
                .counterAgentTypeName("vidra3")
                .build();

        List<CounterAgentType> counterAgentTypes = new ArrayList<CounterAgentType>();
        counterAgentTypes.add(counterAgentType1);
        counterAgentTypes.add(counterAgentType2);
        counterAgentTypes.add(counterAgentType3);

        return counterAgentTypes;
    }

    CounterAgentType createCounterAgentType() {
        final CounterAgentType counterAgentType = CounterAgentType.builder().
                idCounterAgentType(20L).
                counterAgentTypeName("test").build();

        return counterAgentType;
    }

    PagingRequest createRequest() throws JsonProcessingException {
        String json = "{\"draw\":21,\"columns\":[{\"data\":\"idCounterAgentType\",\"name\":\"\",\"searchable\":true,\"orderable\":true,\"search\":{\"value\":\"\",\"regexp\":false}},{\"data\":\"counterAgentTypeName\",\"name\":\"\",\"searchable\":true,\"orderable\":true,\"search\":{\"value\":\"wg\",\"regexp\":false}},{\"data\":\"counterAgentTypeComment\",\"name\":\"\",\"searchable\":true,\"orderable\":true,\"search\":{\"value\":\"\",\"regexp\":false}}],\"order\":[{\"column\":0,\"dir\":\"desc\"}],\"start\":0,\"length\":10,\"search\":{\"value\":\"\",\"regexp\":false}}";

        PagingRequest pagingRequest = objectMapper.readValue(json,PagingRequest.class);
        return pagingRequest;
    }

    @Test
    void find_all_if_string_to_find_is_null() throws JsonProcessingException {

        List<CounterAgentType> counterAgentTypes = createCounterAgentTypes();

        org.springframework.data.domain.Page<CounterAgentType> pagedResponse = new PageImpl<>(counterAgentTypes);

        PagingRequest pagingRequest = createRequest();
        pagingRequest.getColumns().get(1).getSearch().setValue(null);

        Mockito.when(repository.findAll(any(Pageable.class))).thenReturn(pagedResponse);
        Page page = counterAgentTypeService.findAll(pagingRequest);
        Assertions.assertThat(page.getData().equals(counterAgentTypes));
    }

    @Test
    void find_all_if_string_to_find_is_not_null() throws JsonProcessingException {
        List<CounterAgentType> counterAgentTypes = createCounterAgentTypes();

        String content = "obr";

        counterAgentTypes.removeIf(organType -> !organType.getCounterAgentTypeName().contains(content));

        PagingRequest pagingRequest = createRequest();
        pagingRequest.getColumns().get(1).getSearch().setValue(content);

        org.springframework.data.domain.Page<CounterAgentType> pagedResponse = new PageImpl<>(counterAgentTypes);

        Mockito.when(repository.findByCounterAgentTypeNameContainingIgnoreCase(any(String.class), any(Pageable.class))).thenReturn(pagedResponse);
        Page page = counterAgentTypeService.findAll(pagingRequest);
        Assertions.assertThat(page.getData().equals(counterAgentTypes));
    }

    @Test
    void edit_counter_agent_type_if_new_counter_agent_type_equal() {
        CounterAgentType oldCounterAgentType = createCounterAgentType();
        CounterAgentType newCounterAgentType = createCounterAgentType();

        Mockito.when(repository.getOne(any(Long.class))).thenReturn(oldCounterAgentType);
        boolean res = counterAgentTypeService.editCounterAgentType(oldCounterAgentType.getIdCounterAgentType(), newCounterAgentType);
        assert !res;
    }

    @Test
    void edit_counter_agent_type_if_new_counter_agent_type_is_not_equal() {
        CounterAgentType oldCounterAgentType = createCounterAgentType();
        CounterAgentType newCounterAgentType = createCounterAgentType();
        newCounterAgentType.setCounterAgentTypeComment("deer");

        Mockito.when(repository.getOne(any(Long.class))).thenReturn(oldCounterAgentType);
        Mockito.when(repository.save(any(CounterAgentType.class))).thenReturn(newCounterAgentType);
        boolean res = counterAgentTypeService.editCounterAgentType(oldCounterAgentType.getIdCounterAgentType(), newCounterAgentType);
        assert res;
    }

    @Test
    void edit_counter_agent_type_if_new_counter_agent_type_name_is_busy() {
        CounterAgentType oldCounterAgentType = createCounterAgentType();
        CounterAgentType newCounterAgentType = createCounterAgentType();
        newCounterAgentType.setCounterAgentTypeName("vidra3");

        Mockito.when(repository.existsCounterAgentTypeByCounterAgentTypeNameIgnoreCase(any(String.class))).thenReturn(true);
        Mockito.when(repository.getOne(any(Long.class))).thenReturn(oldCounterAgentType);
        Mockito.when(repository.save(any(CounterAgentType.class))).thenReturn(newCounterAgentType);

        boolean res = counterAgentTypeService.editCounterAgentType(oldCounterAgentType.getIdCounterAgentType(), newCounterAgentType);
        assert !res;
    }

    @Test
    void edit_counter_agent_type_if_new_counter_agent_type_name_is_free() {
        CounterAgentType oldCounterAgentType = createCounterAgentType();
        CounterAgentType newCounterAgentType = createCounterAgentType();
        newCounterAgentType.setCounterAgentTypeName("deer");

        Mockito.when(repository.existsCounterAgentTypeByCounterAgentTypeNameIgnoreCase(any(String.class))).thenReturn(false);
        Mockito.when(repository.getOne(any(Long.class))).thenReturn(oldCounterAgentType);
        Mockito.when(repository.save(any(CounterAgentType.class))).thenReturn(newCounterAgentType);

        boolean res = counterAgentTypeService.editCounterAgentType(oldCounterAgentType.getIdCounterAgentType(), newCounterAgentType);
        assert res;
    }
}