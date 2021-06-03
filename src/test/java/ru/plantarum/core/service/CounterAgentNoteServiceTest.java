package ru.plantarum.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.plantarum.core.entity.*;
import ru.plantarum.core.repository.CounterAgentNoteRepository;
import ru.plantarum.core.repository.OrganTypeRepository;
import ru.plantarum.core.repository.ProductRepository;
import ru.plantarum.core.utils.search.CriteriaUtils;
import ru.plantarum.core.web.paging.Page;
import ru.plantarum.core.web.paging.PagingRequest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class CounterAgentNoteServiceTest {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    private final CounterAgentNoteRepository repository = Mockito.mock(CounterAgentNoteRepository.class);
    private final CriteriaUtils criteriaUtils = Mockito.mock(CriteriaUtils.class);

    private final CounterAgentNoteService counterAgentNoteService =
            new CounterAgentNoteService(repository, criteriaUtils);

    CounterAgentType getCounterAgentType() {
        CounterAgentType counterAgentType = CounterAgentType.builder()
                .idCounterAgentType(55L)
                .counterAgentTypeName("Тип")
                .counterAgentTypeComment("hello")
                .build();

        return counterAgentType;
    }

    CounterAgent getCounterAgent() {
        CounterAgent counterAgent = CounterAgent.builder()
                .idCounterAgent(90L)
                .counterAgentName("Имя")
                .counterAgentPhone("нет")
                .counterAgentProfile("нет")
                .counterAgentType(getCounterAgentType())
                .build();

        return counterAgent;
    }

    List<CounterAgentNote> createCounterAgentNotes() {
        final CounterAgentNote counterAgentNote1 = CounterAgentNote.builder()
                .idCounterAgentNote(20L)
                .counterAgent(getCounterAgent())
                .note("bobr1")
                .build();

        final CounterAgentNote counterAgentNote2 = CounterAgentNote.builder()
                .idCounterAgentNote(20L)
                .counterAgent(getCounterAgent())
                .note("bobr2")
                .build();

        final CounterAgentNote counterAgentNote3 = CounterAgentNote.builder()
                .idCounterAgentNote(20L)
                .counterAgent(getCounterAgent())
                .note("vidr3")
                .build();

        List<CounterAgentNote> counterAgentNotes = new ArrayList<CounterAgentNote>();
        counterAgentNotes.add(counterAgentNote1);
        counterAgentNotes.add(counterAgentNote2);
        counterAgentNotes.add(counterAgentNote3);

        return counterAgentNotes;
    }

    CounterAgentNote createCounterAgentNote() {
        final CounterAgentNote counterAgentNote = CounterAgentNote.builder()
                .idCounterAgentNote(20L)
                .counterAgent(getCounterAgent())
                .note("test")
                .build();

        return counterAgentNote;
    }

    PagingRequest createRequest() throws JsonProcessingException {
        String json = "{\"draw\":21,\"columns\":[{\"data\":\"idCounterAgentNote\",\"name\":\"\",\"searchable\":true,\"orderable\":true,\"search\":{\"value\":\"\",\"regexp\":false}},{\"data\":\"counterAgent.counterAgentName\",\"name\":\"\",\"searchable\":true,\"orderable\":true,\"search\":{\"value\":\"\",\"regexp\":false}},{\"data\":\"counterAgent.counterAgentProfile\",\"name\":\"\",\"searchable\":true,\"orderable\":true,\"search\":{\"value\":\"\",\"regexp\":false}},{\"data\":\"counterAgent.counterAgentPhone\",\"name\":\"\",\"searchable\":true,\"orderable\":true,\"search\":{\"value\":\"wg\",\"regexp\":false}},{\"data\":\"note\",\"name\":\"\",\"searchable\":true,\"orderable\":true,\"search\":{\"value\":\"\",\"regexp\":false}}],\"order\":[{\"column\":0,\"dir\":\"desc\"}],\"start\":0,\"length\":10,\"search\":{\"value\":\"\",\"regexp\":false}}";

        PagingRequest pagingRequest = objectMapper.readValue(json,PagingRequest.class);
        return pagingRequest;
    }

    @Test
    void find_all() throws JsonProcessingException {

        List<CounterAgentNote> counterAgentNotes = createCounterAgentNotes();

        org.springframework.data.domain.Page<CounterAgentNote> pagedResponse = new PageImpl<>(counterAgentNotes);

        PagingRequest pagingRequest = createRequest();
        pagingRequest.getColumns().get(1).getSearch().setValue(null);

        Mockito.when(repository.findAll(any(Pageable.class))).thenReturn(pagedResponse);
        Page page = counterAgentNoteService.findAll(pagingRequest);
        Assertions.assertThat(page.getData().equals(counterAgentNotes));
    }


    @Test
    void edit_counter_agent_note_if_new_counter_agent_note_equal() {
        CounterAgentNote oldCounterAgentNote = createCounterAgentNote();
        CounterAgentNote newCounterAgentNote = createCounterAgentNote();

        Mockito.when(repository.getOne(any(Long.class))).thenReturn(oldCounterAgentNote);
        boolean res = counterAgentNoteService.editCounterAgentNote(oldCounterAgentNote.getIdCounterAgentNote(), newCounterAgentNote);
        assert !res;
    }

    @Test
    void edit_counter_agent_note_if_new_counter_agent_note_not_equal() {
        CounterAgentNote oldCounterAgentNote = createCounterAgentNote();
        CounterAgentNote newCounterAgentNote = createCounterAgentNote();
        newCounterAgentNote.setNote("deer");

        Mockito.when(repository.getOne(any(Long.class))).thenReturn(oldCounterAgentNote);
        Mockito.when(repository.save(any(CounterAgentNote.class))).thenReturn(newCounterAgentNote);
        boolean res = counterAgentNoteService.editCounterAgentNote(oldCounterAgentNote.getIdCounterAgentNote(), newCounterAgentNote);
        assert res;
    }
}