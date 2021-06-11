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

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/operationlists")
public class OperationListController {
    private final OperationListService operationListService;
    private final OperationListStatusService operationListStatusService;
    private final OperationTypeService operationTypeService;
    private final CounterAgentService counterAgentService;
    private final ProductService productService;
    private final OperationRowService operationRowService;

    private List<OperationListStatus> getOperationListStatusList() {
        return operationListStatusService.findAllActive();
    }

    private List<OperationType> getOperationTypesList() {
        return operationTypeService.findAll();
    }

    private List<CounterAgent> getCounterAgentsList() {
        return counterAgentService.findAll();
    }

    private List<Product> getProductsList() {
        return productService.findAll();
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
    public String addOperationListForm(@RequestParam(required = false) Long id, Model model) {
        OperationList operationList = OperationList.builder().build();
        if (id != null) {
            operationList = operationListService.getOne(id).orElseThrow(() ->
                    new EntityNotFoundException(String.format("#operation-list-form:  entity by id %s  not found", id)));
        }
        model.addAttribute("operationList", operationList);
        model.addAttribute("operationListStatuses", getOperationListStatusList());
        model.addAttribute("operationTypes", getOperationTypesList());
        model.addAttribute("counterAgents", getCounterAgentsList());
        model.addAttribute("operationRows", operationList.getOperationRows());
        model.addAttribute("products", getProductsList());
        return "add-operation";
    }

    @PostMapping("/edit")
    public String editOperationList(@RequestParam Long id, @Valid @RequestBody OperationList operationList,
                                    BindingResult bindingResult, Model model) {
        if (!operationListService.exists(id)) {
            throw new EntityNotFoundException(String.format("#operation-list-form:  entity by id %s  not found", id));
        }
        operationList.setIdOperationList(id);
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors();
            operationList.setOperationType(null);
            model.addAttribute("operationList", operationList);
            model.addAttribute("operationListStatuses", getOperationListStatusList());
            model.addAttribute("operationTypes", getOperationTypesList());
            model.addAttribute("counterAgents", getCounterAgentsList());
            model.addAttribute("products", getProductsList());
            return "add-operation :: operation-list-form";
        }
        final List<OperationRow> operationRows = operationList.getOperationRows();
        operationRows.forEach(operationRow -> operationRow.setOperationList(operationList));
        operationListService.save(operationList);
        return "redirect:/operationlists/all";

    }

    @PostMapping("/add")
    public String addOperationList(@Valid @RequestBody OperationList operationList,
                                   BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors();
            operationList.setOperationType(null);
            model.addAttribute("operationList", operationList);
            model.addAttribute("operationListStatuses", getOperationListStatusList());
            model.addAttribute("operationTypes", getOperationTypesList());
            model.addAttribute("counterAgents", getCounterAgentsList());
            model.addAttribute("products", getProductsList());
            return "add-operation :: operation-list-form";
        }
        final List<OperationRow> operationRows = operationList.getOperationRows();
        operationRows.forEach(operationRow -> operationRow.setOperationList(operationList));
        operationListService.save(operationList);
        return "redirect:/operationlists/all";
    }

}
