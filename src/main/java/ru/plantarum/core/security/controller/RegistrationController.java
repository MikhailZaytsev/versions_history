//package ru.plantarum.core.security.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import ru.plantarum.core.security.entity.Person;
//import ru.plantarum.core.security.service.PersonService;
//
//import javax.validation.Valid;
//
//@Controller
//@RequiredArgsConstructor
//public class RegistrationController {
//
//    private final PersonService personService;
//
//    @GetMapping("/registry")
//    public String registration(Model model) {
//        Person person = Person.builder().build();
//        model.addAttribute("person", person);
//
//        return "registration";
//    }
//
//    @PostMapping("/registry")
//    public String addPerson(@Valid Person person, BindingResult bindingResult,
//                            Model model) {
//        if (bindingResult.hasErrors()) {
//            return "registration";
//        }
//
//        if (!person.getPassword().equals(person.getPasswordConfirm())) {
//            return "registration";
//        }
//        if (!personService.savePerson(person)) {
//            return "registration";
//        }
//
//        return "redirect:/";
//    }
//}
