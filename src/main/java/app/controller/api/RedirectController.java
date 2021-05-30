package app.controller.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/redirect")
public class RedirectController {

    @GetMapping
    public String get() {
        return "redirect:/";
    }

}
