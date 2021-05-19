package ru.plantarum.core.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.plantarum.core.entity.*;
import ru.plantarum.core.service.*;
import ru.plantarum.core.web.paging.Page;
import ru.plantarum.core.web.paging.PagingRequest;

import javax.jws.WebParam;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/operationlists")
public class OperationListController {
    private final OperationListService operationListService;
    private final OperationListStatusService operationListStatusService;
    private final OperationTypeService operationTypeService;
    private final CounterAgentService counterAgentService;

    private List<OperationListStatus> getOperationListStatusList() {
        return operationListStatusService.findAllActive();
    }

    private List<OperationType> getOperationTypesList() {
        return operationTypeService.findAll();
    }

    private List<CounterAgent> getCounterAgentsList() {
        return counterAgentService.findAll();
    }

    @PostMapping
    @ResponseBody
    public Page<OperationList> list(@RequestBody PagingRequest pagingRequest) {
        return operationListService.findAll(pagingRequest);
    }

    @GetMapping("/all")
    public String showAllOperationLists() {
        return "show-all-operation-lists";
    }

    @GetMapping({"/add", "/edit"})
        public String addOperationListForm(@RequestParam (required = false) Long id, Model model) {
        OperationList operationList = OperationList.builder().build();
        if (id != null) {
            operationList = operationListService.getOne(id).orElseThrow(() ->
                    new EntityNotFoundException(String.format("#editOperationListForm:  entity by id %s  not found", id)));
        }
        model.addAttribute("operationList", operationList);
        model.addAttribute("operationListStatuses", getOperationListStatusList());
        model.addAttribute("operationTypes", getOperationTypesList());
        model.addAttribute("counterAgents", getCounterAgentsList());
        return "add-operation-list";
    }

    @PostMapping("/edit")
    public String editOperationList(@RequestParam Long id, @Valid OperationList operationList,
                                    BindingResult bindingResult, Model model) {
        if (!operationListService.exists(id)) {
            throw new EntityNotFoundException(String.format("#editOperationlistForm:  entity by id %s  not found", id));
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("operationListStatuses", getOperationListStatusList());
            operationList.setIdOperationList(id);
            model.addAttribute("operationList", operationList);
            return "add-operation-list";
        }
        if (operationListService.edit(id, operationList)) {
            return "redirect:/operationlists/all";
        } else {
            //TODO исправить rejectvalue
            bindingResult.rejectValue("operationListComment", "", "Что-то пошло не так");
            model.addAttribute("operationListStatuses", getOperationListStatusList());
            model.addAttribute("operationTypes", getOperationTypesList());
            model.addAttribute("counterAgents", getCounterAgentsList());
            operationList.setIdOperationList(id);
            model.addAttribute("operationList", operationList);
            return "add-operation-list";
        }
    }

    @PostMapping("/add")
    public String addOperationList(@Valid @ModelAttribute("operationList") OperationList operationList,
                                   BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("operationListStatuses", getOperationListStatusList());
            model.addAttribute("operationTypes", getOperationTypesList());
            model.addAttribute("counterAgents", getCounterAgentsList());
            return "add-operation-list";
        }
        operationListService.save(operationList);
        return "redirect:/operationrows/add?id=" + operationList.getIdOperationList();
    }

}
