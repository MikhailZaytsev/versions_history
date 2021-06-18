package ru.plantarum.core.security.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.plantarum.core.security.service.PersonService;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final PersonService personService;

    @GetMapping("/admin")
    public String userList(Model model){
        model.addAttribute("allUsers", personService.allPersons());
        return "admin";
    }

    @PostMapping("/admin")
    public String deleteUser(@RequestParam(required = true, defaultValue = "" ) Long userId,
                             @RequestParam(required = true, defaultValue = "") String action,
                             Model model) {
        if (action.equals("delete")) {
            personService.deletePerson(userId);
        }
        return "redirect:/admin";
    }

}
