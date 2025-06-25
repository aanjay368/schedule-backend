package id.my.schedule.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PageController implements ErrorController {

    @RequestMapping(value = "/")
    public ModelAndView login(){
        return new ModelAndView("index");
    }

    @GetMapping(path = "/error")
    public ModelAndView handleError(){
        return new ModelAndView("index");
    }

}
