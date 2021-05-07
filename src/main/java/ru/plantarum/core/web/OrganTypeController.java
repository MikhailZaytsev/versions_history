package ru.plantarum.core.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.plantarum.core.entity.OrganType;
import ru.plantarum.core.service.OrganTypeService;
import ru.plantarum.core.web.paging.Page;
import ru.plantarum.core.web.paging.PagingRequest;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

@Controller
@RequestMapping("/organtypes")
@RequiredArgsConstructor
public class OrganTypeController {
    private final OrganTypeService organTypeService;

    @PostMapping
    @ResponseBody
    public Page<OrganType> list(@RequestBody PagingRequest pagingRequest) {
        return organTypeService.findAll(pagingRequest);
    }

    @GetMapping("/all")
    public String showAllOrganTypes() {
        return "show-all-organ-types";
    }

    @GetMapping({"/add", "/edit"})
    public String addOrganTypeForm(@RequestParam(required = false) Long id, Model model) {
        OrganType organType = OrganType.builder().build();
        if (id != null) {
            organType = organTypeService.getOne(id).orElseThrow(() ->
                    new EntityNotFoundException(String.format("#EditOrganTypeForm: entity by id %s not found", id)));
        }
        model.addAttribute("organType", organType);
        return "add-organ-type";
    }

    @PostMapping("/edit")
    public String editOrganType(@RequestParam Long id, @Valid OrganType organType,
                                BindingResult bindingResult, Model model) {
        if (!organTypeService.exists(id)) {
            throw new EntityNotFoundException(String.format("#editOrganTypeForm:  entity by id %s not found", id));
        }
        if (bindingResult.hasErrors()){
            organType.setIdOrganType(id);
            model.addAttribute("organType", organType);
            return "add-organ-type";
        }
        if (organTypeService.editOrganType(id, organType)) {
            return "redirect:/organtypes/all";
        }
        else {
            bindingResult.rejectValue("OrganTypeName", "", "Уже существует");
            return "add-organ-type";
        }
    }

    @PostMapping("/add")
    public String addOrganType(@Valid OrganType organType, BindingResult bindingResult) {
        if (organTypeService.findByOrganTypeName(organType.getOrganTypeName()) != null) {
            bindingResult.rejectValue("organTypeName", "", "Уже существует");
        }
        if (bindingResult.hasErrors()) {
            return "add-organ-type";
        }
        organTypeService.save(organType);
        return "redirect:/organtypes/all";
    }
}
