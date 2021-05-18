package ru.plantarum.core.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.plantarum.core.entity.CounterAgentType;
import ru.plantarum.core.repository.CounterAgentTypeRepository;
import ru.plantarum.core.service.CounterAgentTypeService;
import ru.plantarum.core.web.paging.Page;
import ru.plantarum.core.web.paging.PagingRequest;

import javax.jws.WebParam;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

@Controller
@RequestMapping("/counteragenttypes")
@RequiredArgsConstructor
public class CounterAgentTypeController {
    private final CounterAgentTypeService counterAgentTypeService;

    @PostMapping
    @ResponseBody
    public Page<CounterAgentType> list(@RequestBody PagingRequest pagingRequest) {
        return counterAgentTypeService.findAll(pagingRequest);
    }

    @GetMapping("/all")
    public String showAllCounterAgentTypes() {
        return "show-all-counter-agent-types";
    }

    @GetMapping({"/add", "/edit"})
    public String addCounterAgentTypeForm(@RequestParam(required = false) Long id, Model model) {
        CounterAgentType counterAgentType = CounterAgentType.builder().build();
        if (id != null) {
            counterAgentType = counterAgentTypeService.getOne(id).orElseThrow(() ->
                    new EntityNotFoundException(String.format("#EditCounterAgentTypeForm: entity by id %s not found", id)));
        }
        model.addAttribute("counterAgentType", counterAgentType);
        return "add-counter-agent-type";
    }

    @PostMapping("/edit")
    public String editCounterAgentType(@RequestParam Long id, @Valid CounterAgentType counterAgentType,
                                       BindingResult bindingResult, Model model) {
        if(!counterAgentTypeService.exists(id)) {
            throw new EntityNotFoundException(String.format("#EditCounterAgentTypeForm: entity by id %s not found", id));
        }
        if (bindingResult.hasErrors()) {
            counterAgentType.setIdCounterAgentType(id);
            model.addAttribute("counterAgentType", counterAgentType);
            return "add-counter-agent-type";
        }
        if (counterAgentTypeService.editCounterAgentType(id, counterAgentType)) {
            return "redirect:/counteragenttypes/all";
        }
        else {
            bindingResult.rejectValue("counterAgentTypeName", "", "Уже существует");
            return "add-counter-agent-type";
        }
    }

    @PostMapping("/add")
    public String addCounterAgentType(@Valid @ModelAttribute("counterAgentType") CounterAgentType counterAgentType, BindingResult bindingResult) {
        if (counterAgentTypeService.findByCounterAgentTypeName(counterAgentType.getCounterAgentTypeName()) != null) {
            bindingResult.rejectValue("counterAgentTypeName", "", "Уже существует");
        }
        if (bindingResult.hasErrors()) {
            return "add-counter-agent-type";
        }
        counterAgentTypeService.save(counterAgentType);
        return "redirect:/counteragenttypes/all";
    }
}
