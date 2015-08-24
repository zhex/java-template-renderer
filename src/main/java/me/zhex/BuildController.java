package me.zhex;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BuildController {
    @RequestMapping("/test")
    public String test(@RequestParam("template") String template) {
        return template;
    }
}
