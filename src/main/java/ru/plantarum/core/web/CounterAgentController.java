package ru.plantarum.core.web;


import com.sun.org.apache.xpath.internal.operations.Mod;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.rsocket.context.LocalRSocketServerPort;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.plantarum.core.entity.CounterAgent;
import ru.plantarum.core.entity.CounterAgentNote;
import ru.plantarum.core.entity.CounterAgentType;
import ru.plantarum.core.service.CounterAgentNoteService;
import ru.plantarum.core.service.CounterAgentService;
import ru.plantarum.core.service.CounterAgentTypeService;
import ru.plantarum.core.web.paging.Page;
import ru.plantarum.core.web.paging.PagingRequest;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;
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

    private List<CounterAgentNote> getCounterAgentNoteList(CounterAgent counterAgent) {
        List<CounterAgentNote> notes = counterAgentNoteService.findById(counterAgent.getIdCounterAgent());
        if (notes.isEmpty()) {
            CounterAgentNote note = CounterAgentNote.builder()
                    .idCounterAgentNote(999L)
                    .note("Заметок нет")
                    .counterAgent(counterAgent).build();
            notes.add(note);
        }
        Collections.reverse(notes);
        return notes;
    }

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
        CounterAgentNote counterAgentNote = CounterAgentNote.builder().build();
// попытка реализовать добавление комментариев к определенному контрагенту
        if (id != null) {
            counterAgent = counterAgentService.getOne(id).orElseThrow(() ->
                    new EntityNotFoundException(String.format("#editCounterAgentForm:  entity by id %s  not found", id)));
            model.addAttribute("counterAgentNotes", getCounterAgentNoteList(counterAgent));
        }
        model.addAttribute("counterAgentNote", counterAgentNote);
        model.addAttribute("counterAgent", counterAgent);
        model.addAttribute("counterAgentTypes", getCounterAgentTypesList());
        return "add-counter-agent";
/*        if (!counterAgentService.exists(id)) {
            model.addAttribute("counterAgent", counterAgent);
            model.addAttribute("counterAgentTypes", getCounterAgentTypesList());
        } else {
            counterAgent = counterAgentService.getOne(id);
            model.addAttribute("counterAgent", counterAgent);
            model.addAttribute("counterAgentTypes", getCounterAgentTypesList());
            model.addAttribute("counterAgentsNotes", getCounterAgentNoteList(id));
        }
        return "add-counter-agent";*/
    }

    @GetMapping("/delete")
    public String deleteCounterAgent(@RequestParam Long id) {
        if (counterAgentService.exists(id)) {
            counterAgentService.save(counterAgentService.deleteCounterAgent(id));
            return "redirect:/counteragents/all";
//            try {
//                counterAgentService.save(counterAgentService.deleteCounterAgent(id));
//                return "redirect:/counteragents/all";
//            } catch (Exception e) {
//                return "redirect:/counteragents/all";
//            }
        }
        return "redirect:/counteragents/all";
    }

    @PostMapping("/edit")
    public String editCounterAgent(@RequestParam Long id, @Valid CounterAgent counterAgent,
                                   BindingResult bindingResult, Model model) {
        boolean exists = counterAgentService.exists(id);
        if (!exists) {
            throw new EntityNotFoundException(String.format("#editCounterAgentForm:  entity by id %s  not found", id));
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("counterAgentTypes", getCounterAgentTypesList());
            counterAgent.setIdCounterAgent(id);
            model.addAttribute("counterAgent", counterAgent);
            return "add-counter-agent";
        }
        boolean request = false;
        try {
            request = counterAgentService.editCounterAgent(id, counterAgent);
        } catch (DataIntegrityViolationException e) {
            bindingResult.rejectValue("counterAgentName", "", "Контрагент уже существует");
            bindingResult.rejectValue("counterAgentProfile", "", "Контрагент уже существует");
            bindingResult.rejectValue("counterAgentPhone", "", "Контрагент уже существует");
            model.addAttribute("counterAgentTypes", getCounterAgentTypesList());
            return "add-counter-agent";
        }
        if (request) {
            return "redirect:/counteragents/all";
        } else {
            bindingResult.rejectValue("counterAgentName", "", "Внесите изменения");
            model.addAttribute("counterAgentTypes", getCounterAgentTypesList());
            return "add-counter-agent";
        }
    }

    @PostMapping("/add")
    public String addCounterAgent(@Valid @ModelAttribute("counterAgent") CounterAgent counterAgent,
                                  BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("counterAgentTypes", getCounterAgentTypesList());
            return "add-counter-agent";
        }
        try {
            counterAgentService.save(counterAgent);
            return "redirect:/counteragents/all";
        } catch (DataIntegrityViolationException e) {
            bindingResult.rejectValue("counterAgentName", "", "Контрагент уже существует");
            bindingResult.rejectValue("counterAgentProfile", "", "Контрагент уже существует");
            bindingResult.rejectValue("counterAgentPhone", "", "Контрагент уже существует");
            model.addAttribute("counterAgentTypes", getCounterAgentTypesList());
            return "add-counter-agent";
        }
    }

    @PostMapping("/add-note/{counterAgentId}")
    public String addComment(@PathVariable("counterAgentId") Long id, String note) {
        CounterAgent counterAgent = counterAgentService.getOne(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("#editCounterAgentForm:  entity by id %s  not found", id)));
        CounterAgentNote counterAgentNote = CounterAgentNote.builder()
                .note(note)
                .counterAgent(counterAgent)
                .build();
        counterAgentNoteService.save(counterAgentNote);
        return "redirect:/counteragents/edit?id=" + id;
    }
}
