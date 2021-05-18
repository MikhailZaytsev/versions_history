package ru.plantarum.core.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.plantarum.core.entity.OperationListStatus;
import ru.plantarum.core.service.OperationListStatusService;
import ru.plantarum.core.web.paging.Page;
import ru.plantarum.core.web.paging.PagingRequest;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping("/operationliststatuses")
public class OperationListStatusController {
    private final OperationListStatusService operationListStatusService;

    @PostMapping
    @ResponseBody
    public Page<OperationListStatus> list(@RequestBody PagingRequest pagingRequest) {
        return operationListStatusService.findAll(pagingRequest);
    }

    @GetMapping("/all")
    public String showAllOperationListStatuses() {
        return "show-all-operation-list-statuses";
    }

    @GetMapping({"/add", "/edit"})
    public String addOperationListStatusForm(@RequestParam(required = false) Long id, Model model) {
        OperationListStatus operationListStatus = OperationListStatus.builder().build();
        if (id != null) {
            operationListStatus = operationListStatusService.getOne(id).orElseThrow(() ->
                    new EntityNotFoundException(String.format("#EditOperationListStatusForm: entity by id %s not found", id)));
        }
        model.addAttribute("operationListStatus", operationListStatus);
        return "add-operation-list-status";
    }

    @PostMapping("/edit")
    public String editOperationListStatus(@RequestParam Long id, @Valid OperationListStatus operationListStatus,
                                          BindingResult bindingResult, Model model) {
        if (!operationListStatusService.exists(id)) {
            throw new EntityNotFoundException(String.format("#EditOperationListStatusForm: entity by id %s not found", id));
        }
        if (bindingResult.hasErrors()) {
            operationListStatus.setIdOperationListStatus(id);
            model.addAttribute("operationListStatus", operationListStatus);
            return "add-operation-list-status";
        }
        if (operationListStatusService.editOperationListStatus(id, operationListStatus)) {
            return "redirect:/operationliststatuses/all";
        }
        else {
            bindingResult.rejectValue("operationListStatusName", "", "Статус не изменён");
            return "add-operation-list-status";
        }
    }

    @PostMapping("/add")
    public String addOperationListStatus(@Valid @ModelAttribute("operationListStatus") OperationListStatus operationListStatus,
                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "add-operation-list-status";
        }
        else {
            operationListStatusService.save(operationListStatus);
            return "redirect:/operationliststatuses/all";
        }
    }
}
