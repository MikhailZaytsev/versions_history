package ru.plantarum.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.plantarum.core.entity.CounterAgentNote;
import ru.plantarum.core.repository.CounterAgentNoteRepository;
import ru.plantarum.core.web.paging.Direction;
import ru.plantarum.core.web.paging.Order;
import ru.plantarum.core.web.paging.PagingRequest;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CounterAgentNoteService {
    private final CounterAgentNoteRepository counterAgentNoteRepository;

    List<CounterAgentNote> findAll() {
      return counterAgentNoteRepository.findAll();
    }

    public List<CounterAgentNote> findById(Long id) {
        return counterAgentNoteRepository.findByCounterAgentIdCounterAgent(id);
    }

    public ru.plantarum.core.web.paging.Page<CounterAgentNote> findAll(PagingRequest pagingRequest) {
        String stringToFind = pagingRequest.getColumns().get(1).getSearch().getValue();

        int pageNumber = pagingRequest.getStart() / pagingRequest.getLength();
        Order order = pagingRequest.getOrder().stream()
                .findFirst()
                .orElse(new Order(0, Direction.desc));
        String colToOrder = pagingRequest.getColumns().get(order.getColumn()).getData();
        final PageRequest pageRequest = PageRequest.of(pageNumber, pagingRequest.getLength(),
                Sort.Direction.fromString(order.getDir().name()), colToOrder);
        final Page<CounterAgentNote> counterAgentNotes = counterAgentNoteRepository.findAll(pageRequest);
        ru.plantarum.core.web.paging.Page<CounterAgentNote> page = new ru.plantarum.core.web.paging.Page<>(counterAgentNotes);
        page.setDraw(pagingRequest.getDraw());
        return page;
     }

     public boolean editCounterAgentNote(Long id, CounterAgentNote newCounterAgentNote) {
        CounterAgentNote counterAgentNote = counterAgentNoteRepository.getOne(id);
        newCounterAgentNote.setIdCounterAgentNote(id);
        if (!counterAgentNote.equals(newCounterAgentNote)) {
            counterAgentNoteRepository.save(newCounterAgentNote);
            return true;
        }
        return false;
     }

     public Optional<CounterAgentNote> getOne(Long id) {
        return Optional.of(counterAgentNoteRepository.getOne(id));
     }

     public boolean exists(Long id) {
        return counterAgentNoteRepository.existsById(id);
     }

     public CounterAgentNote save(CounterAgentNote counterAgentNote) {
        return counterAgentNoteRepository.save(counterAgentNote);
     }

}
