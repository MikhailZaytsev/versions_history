package ru.plantarum.core.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.plantarum.core.entity.OperationType;
import ru.plantarum.core.service.OperationTypeService;
import ru.plantarum.core.web.paging.Page;
import ru.plantarum.core.web.paging.PagingRequest;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/operationtypes")
@RequiredArgsConstructor
public class OperationTypeController {

    private final OperationTypeService operationTypeService;

    @PostMapping
    @ResponseBody
    public Page<OperationType> list(@RequestBody PagingRequest pagingRequest) {
        return operationTypeService.findAll(pagingRequest);
    }

    @GetMapping
    public String showAllOperationTypes(Model model) {
        List<OperationType> operationTypes = operationTypeService.findAll();
        model.addAttribute("operationTypes", operationTypes);
        return "index";
    }

    @GetMapping("/all")
    public String showAllOperationTypes() {
        return "show-all-operationtypes";
    }

    @GetMapping({"/add", "/edit"})
    public String addOperationTypeForm(@RequestParam(required = false) Long id, Model model) {
        OperationType operationType = OperationType.builder().build();
        if (id != null) {
            operationType = operationTypeService.getOne(id).orElseThrow(() ->
                    new EntityNotFoundException(String.format("#EditOperationTypeForm: entity by id %s not found", id)));
        }
        model.addAttribute(operationType);
        return "add-operationtype";
    }

    @PostMapping("/edit")
    public String editOperationType(@RequestParam Long id, @Valid OperationType operationType,
                                    BindingResult bindingResult, Model model) {
        if (!operationTypeService.exists(id)) {
            throw new EntityNotFoundException(String.format("#editOperationtypeForm:  entity by id %s not found", id));
        }
        if (bindingResult.hasErrors()) {
            operationType.setIdOperationType(id);
            model.addAttribute("operationType", operationType);
            return "add-operationtype";
        }
        if (operationTypeService.editOperationType(id, operationType)) {
            return "redirect:/operationtypes/all";
        }
        else {
            bindingResult.rejectValue("operationTypeName", "", "Уже существует");
            return "add-operationtype";
        }
    }

    @PostMapping("/add")
    public String addOperationType(@Valid OperationType operationType, BindingResult bindingResult) {
        if (operationTypeService.findByOperationTypeName(operationType.getOperationTypeName()) != null) {
            bindingResult.rejectValue("operationTypeName", "", "Уже существует");
        }
        if (bindingResult.hasErrors()) {
            return "add-operationtype";
        }
        operationTypeService.save(operationType);
        return "redirect:/operationtypes/all";
    }

}
