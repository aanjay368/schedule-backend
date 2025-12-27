package id.my.schedule.controller;// src/main/java/com/yourproject/controller/SpaFallbackController.java

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaFalbackController {

    @GetMapping(path = "/")
    public String forwardToIndex() {

        return "forward:/index.html";
    }
}