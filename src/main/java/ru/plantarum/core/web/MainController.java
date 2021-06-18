package ru.plantarum.core.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
public class MainController {

    @GetMapping("/index")
    public String getHomePage() {
        return "start";
    }

    // Login form
//    @RequestMapping("/login")
//    public String login() {
//        return "login";
//    }

}
