package ru.plantarum.core.security.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.plantarum.core.entity.Campaign;
import ru.plantarum.core.security.entity.Person;
import ru.plantarum.core.security.entity.Role;
import ru.plantarum.core.security.service.PersonService;
import ru.plantarum.core.web.paging.Page;
import ru.plantarum.core.web.paging.PagingRequest;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final PersonService personService;

    private List<Role> getRoles() {
        return personService.getRoles();
    }

    @PostMapping
    @ResponseBody
    public Page<Person> userList(@RequestBody PagingRequest pagingRequest){
        return personService.findAll(pagingRequest);
    }

    @GetMapping("/all")
    public String adminForm() {
        return "show-all-persons";
    }

    @GetMapping({"/add", "/edit"})
    public String addPersonForm(@RequestParam(required = false) Long id, Model model) {
        Person person = Person.builder().build();
        if (id != null) {
            person = personService.getOne(id).orElseThrow(() ->
                    new EntityNotFoundException(String.format("#addPersonForm: user with id %s  does  not exists", id)));
        }
        model.addAttribute("rolesList", getRoles());
        model.addAttribute("person", person);
        return "add-person";
    }

//    @PostMapping("/admin")
//    public String deleteUser(@RequestParam(required = true, defaultValue = "" ) Long userId,
//                             @RequestParam(required = true, defaultValue = "") String action,
//                             Model model) {
//        if (action.equals("delete")) {
//            personService.deletePerson(userId);
//        }
//        return "redirect:/admin";
//    }

}
