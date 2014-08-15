package dataxu.intranet.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import dataxu.intranet.entity.Contact;
import dataxu.intranet.model.ClientRepository;
import dataxu.intranet.repository.ContactRepository;

@Controller
class ExampleController {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ContactRepository contactRepository;

    @RequestMapping("/example")
    String example(Model model) {
        model.addAttribute("exampleText", "Welcome to Thymleaf !");
        model.addAttribute("now", new Date());
        model.addAttribute("clients", clientRepository.findAll());

        List<Contact> c = contactRepository.findAll();
        System.out.println(c);
        return "example";
    }

    @RequestMapping("/another/example")
    String example2(Model model) {
        model.addAttribute("exampleText", "Another Thymleaf page...");

        return "example-2";
    }
}