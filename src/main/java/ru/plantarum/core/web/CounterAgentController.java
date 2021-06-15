package ru.plantarum.core.web;


import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.plantarum.core.entity.BareCode;
import ru.plantarum.core.entity.CounterAgent;
import ru.plantarum.core.entity.CounterAgentNote;
import ru.plantarum.core.entity.CounterAgentType;
import ru.plantarum.core.service.CounterAgentNoteService;
import ru.plantarum.core.service.CounterAgentService;
import ru.plantarum.core.service.CounterAgentTypeService;
import ru.plantarum.core.web.paging.Page;
import ru.plantarum.core.web.paging.PagingRequest;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/counteragents")
@RequiredArgsConstructor
public class CounterAgentController {

    private final CounterAgentService counterAgentService;
    private final CounterAgentTypeService counterAgentTypeService;
    private final CounterAgentNoteService counterAgentNoteService;

    private List<CounterAgentType> getCounterAgentTypesList() {
        return counterAgentTypeService.findAll();
    }

//    private List<CounterAgentNote> getCounterAgentNoteList(CounterAgent counterAgent) {
//        List<CounterAgentNote> notes = counterAgentNoteService.findById(counterAgent.getIdCounterAgent());
//        if (notes.isEmpty()) {
//            CounterAgentNote note = CounterAgentNote.builder()
//                    .note("Заметок нет")
//                    .counterAgent(counterAgent).build();
//            notes.add(note);
//        }
//        Collections.reverse(notes);
//        return notes;
//    }

    @PostMapping
    @ResponseBody
    public Page<CounterAgent> list(@RequestBody PagingRequest pagingRequest) {
        return counterAgentService.findAll(pagingRequest);
    }

    @GetMapping("/all")
    public String showAllCounterAgents() {
        return "show-all-counter-agents";
    }

    @GetMapping({"/add", "/edit"})
    public String showCounterAgentForm(@RequestParam(required = false) Long id, Model model) {
        CounterAgent counterAgent = CounterAgent.builder().build();
        if (id != null) {
            counterAgent = counterAgentService.getOne(id).orElseThrow(() ->
                    new EntityNotFoundException(String.format("#counter-agent-form:  entity by id %s  not found", id)));
        }
        model.addAttribute("counterAgent", counterAgent);
        model.addAttribute("counterAgentTypes", getCounterAgentTypesList());
        model.addAttribute("counterAgentNotes", counterAgent.getCounterAgentNotes());
        return "add-counter-agent";
    }

    @GetMapping("/delete")
    public String deleteCounterAgent(@RequestParam Long id) {
        if (counterAgentService.exists(id)) {
            counterAgentService.save(counterAgentService.deleteCounterAgent(id));
        }
        return "redirect:/counteragents/all";
    }

    @PostMapping("/edit")
    public String editCounterAgent(@RequestParam Long id, @Valid @RequestBody CounterAgent counterAgent,
                                   BindingResult bindingResult, Model model) {
        if (!counterAgentService.exists(id)) {
            throw new EntityNotFoundException(String.format("#editCounterAgentForm:  entity by id %s  not found", id));
        }
        counterAgent.setIdCounterAgent(id);
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors();
            model.addAttribute("counterAgent", counterAgent);
            model.addAttribute("counterAgentTypes", getCounterAgentTypesList());
            model.addAttribute("counterAgentNotes", counterAgent.getCounterAgentNotes());
            return "add-counter-agent :: counter-agent-form";
        }
        final List<CounterAgentNote> counterAgentNotes = counterAgent.getCounterAgentNotes();
        counterAgentNotes.forEach(counterAgentNote -> counterAgentNote.setCounterAgent(counterAgent));
        counterAgentService.save(counterAgent);
        return "redirect:/counteragents/all";
    }

    @PostMapping("/add")
    public String addCounterAgent(@Valid @RequestBody CounterAgent counterAgent,
                                  BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors();
            model.addAttribute("counterAgent", counterAgent);
            model.addAttribute("counterAgentTypes", getCounterAgentTypesList());
            model.addAttribute("counterAgentNotes", counterAgent.getCounterAgentNotes());
            return "add-counter-agent :: counter-agent-form";
        }
        final List<CounterAgentNote> counterAgentNotes = counterAgent.getCounterAgentNotes();
        counterAgentNotes.forEach(counterAgentNote -> counterAgentNote.setCounterAgent(counterAgent));
        counterAgentService.save(counterAgent);
        return "redirect:/counteragents/all";
    }

//    @PostMapping("/add-note/{counterAgentId}")
//    @ResponseBody
//    public void addComment(@PathVariable("counterAgentId") Long id, String note) {
//        CounterAgent counterAgent = counterAgentService.getOne(id).orElseThrow(() ->
//                new EntityNotFoundException(String.format("#addComment:  entity by id %s  not found", id)));
//        CounterAgentNote counterAgentNote = CounterAgentNote.builder()
//                .note(note)
//                .counterAgent(counterAgent)
//                .build();
//        counterAgentNoteService.save(counterAgentNote);
//    }
}
