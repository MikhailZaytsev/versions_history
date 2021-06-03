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
                    new EntityNotFoundException(String.format("#editOperationListForm:  entity by id %s  not found", id)));
        }
        model.addAttribute("operationList", operationList);
        model.addAttribute("operationListStatuses", getOperationListStatusList());
        model.addAttribute("operationTypes", getOperationTypesList());
        model.addAttribute("counterAgents", getCounterAgentsList());
        model.addAttribute("operationRows", operationList.getOperationRows());
        model.addAttribute("products", getProductsList());
        return "test-operation";
    }

    //TODO переделать метод редактирования
    @PostMapping("/edit")
    public String editOperationList(@RequestParam Long id, @Valid OperationList operationList,
                                    BindingResult bindingResult, Model model) {
        if (!operationListService.exists(id)) {
            throw new EntityNotFoundException(String.format("#editOperationlistForm:  entity by id %s  not found", id));
        }
        operationList.setIdOperationList(id);
        if (bindingResult.hasErrors()) {
            model.addAttribute("operationListStatuses", getOperationListStatusList());
            operationList.setIdOperationList(id);
            model.addAttribute("operationList", operationList);
            return "test-operation";
        }
        if (operationList.getOperationRows() != null) {
            operationList.getOperationRows().forEach(operationRow -> operationRow.setOperationList(operationList));
        }
        operationListService.save(operationList);
        //operationRowService.saveAll(operationList.getOperationRows());
        return "redirect:/operationlists/all";
//        if (operationListService.edit(id, operationList)) {
//            return "redirect:/operationlists/all";
//        } else {
//            bindingResult.rejectValue("operationListComment", "", "Что-то пошло не так");
//            model.addAttribute("operationListStatuses", getOperationListStatusList());
//            model.addAttribute("operationTypes", getOperationTypesList());
//            model.addAttribute("counterAgents", getCounterAgentsList());
//            operationList.setIdOperationList(id);
//            model.addAttribute("operationList", operationList);
//            return "add-operation-list";
//        }
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
        List<OperationRow> rows = operationList.getOperationRows();
        operationList.setOperationRows(null);

        OperationList saved = operationListService.save(operationList);
        if (rows != null) {
            rows.forEach(operationRow -> operationRow.setOperationList(saved));
            operationRowService.saveAll(rows);
        }


        return "redirect:/operationlists/all";
    }

}
