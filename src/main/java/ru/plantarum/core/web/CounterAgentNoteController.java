package ru.plantarum.core.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.plantarum.core.entity.CounterAgent;
import ru.plantarum.core.entity.CounterAgentNote;
import ru.plantarum.core.service.CounterAgentNoteService;
import ru.plantarum.core.service.CounterAgentService;
import ru.plantarum.core.web.paging.Page;
import ru.plantarum.core.web.paging.PagingRequest;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/counteragentsnotes")
@RequiredArgsConstructor
public class CounterAgentNoteController {
    private final CounterAgentNoteService counterAgentNoteService;
    private final CounterAgentService counterAgentService;

    List<CounterAgent> getCounterAgentList() {
        return counterAgentService.findAll();
    }

    @PostMapping
    @ResponseBody
    public Page<CounterAgentNote> list(@RequestBody PagingRequest pagingRequest) {
         return counterAgentNoteService.findAll(pagingRequest);
    }

    @GetMapping("/all")
    public String showAllCounterAgentNotes() {
        return "show-all-counter-agent-notes";
    }

    @GetMapping({"/add", "/edit"})
    public String showCounterAgentNoteForm(@RequestParam(required = false) Long id, Model model) {
        CounterAgentNote counterAgentNote = CounterAgentNote.builder().build();
        if (id != null) {
            counterAgentNote = counterAgentNoteService.getOne(id).orElseThrow(() ->
                    new EntityNotFoundException(String.format("#EditCounterAgentNoteForm: entity by id %s not found", id)));
        }
        model.addAttribute("counterAgentNote", counterAgentNote);
        model.addAttribute("counterAgents", getCounterAgentList());
        return "add-counter-agent-note";
    }

    @PostMapping("/edit")
    public String editCounterAgentNote(@RequestParam Long id, @Valid CounterAgentNote counterAgentNote,
                                       BindingResult bindingResult, Model model) {
        boolean exists = counterAgentNoteService.exists(id);
        if (!exists) {
            throw new EntityNotFoundException(String.format("#editCounterAgentNoteForm:  entity by id %s  not found", id) );
        }
        if (bindingResult.hasErrors()) {
            counterAgentNote.setIdCounterAgentNote(id);
            model.addAttribute("counterAgents", getCounterAgentList());
            model.addAttribute("counterAgentNote", counterAgentNote);
            return "add-counter-agent-note";
        }
        if (counterAgentNoteService.editCounterAgentNote(id, counterAgentNote)) {
           return "redirect:/counteragentsnotes/all";
        } else {
            bindingResult.rejectValue("note", "", "Не изменено");
            model.addAttribute("counterAgents", getCounterAgentList());
            model.addAttribute("counterAgentNote", counterAgentNote);
            return "add-counter-agent-note";
        }
    }

    @PostMapping("/add")
    public String addCounterAgentNote(@Valid @ModelAttribute("counterAgentNote") CounterAgentNote counterAgentNote,
                                      BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("counterAgents", getCounterAgentList());
            model.addAttribute("counterAgentNote", counterAgentNote);
            return "add-counter-agent-note";
        }
        else {
            counterAgentNoteService.save(counterAgentNote);
            return "redirect:/counteragentsnotes/all";
        }
    }
}
