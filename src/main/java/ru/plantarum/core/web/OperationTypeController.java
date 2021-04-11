package ru.plantarum.core.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.plantarum.core.entity.OperationType;
import ru.plantarum.core.service.OperationTypeService;

import javax.validation.Valid;

@Controller
@RequestMapping("/operation-types")
@RequiredArgsConstructor
public class OperationTypeController {

    private final OperationTypeService service;

    @GetMapping
    public String showAllOperationTypes(Model model) {
        model.addAttribute("operationTypes", service.findAll());
        return "index";
    }

    @GetMapping("/add")
    public String showAdd(Model model) {
        model.addAttribute("operationType", OperationType.builder().build());
        return "add-operation-type";
    }

    @PostMapping("/add")
    public String addOperationType(@Valid OperationType operationType, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "add-operation-type";
        }
        service.save(operationType);
        return "redirect:";
    }

}
