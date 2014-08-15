package dataxu.intranet.controller;

import dataxu.intranet.model.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@Controller
class ExampleController {

    @Autowired
    private ClientRepository clientRepository;

    @RequestMapping("/example")
    String example(Model model) {
        model.addAttribute("exampleText", "Welcome to Thymleaf !");
        model.addAttribute("now", new Date());
        model.addAttribute("clients", clientRepository.findAll());

        return "example";
    }

    @RequestMapping("/another/example")
    String example2(Model model) {
        model.addAttribute("exampleText", "Another Thymleaf page...");

        return "example-2";
    }
}