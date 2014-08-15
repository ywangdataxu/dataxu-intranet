package dataxu.intranet.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import dataxu.intranet.entity.Contact;
import dataxu.intranet.repository.ContactRepository;

@Controller
public class ContactController {
    @Autowired
    private ContactRepository contactRepository;

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public String contacts() {
        return "contacts";
    }

    @RequestMapping(value = "/api/users", method = RequestMethod.GET)
    @ResponseBody
    public List<Contact> getcontacts() {
        List<Contact> contacts = contactRepository.findAll();
        return contacts;
    }

    @RequestMapping(value = "/api/users/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Contact getcontact(@PathVariable("id") Integer id) {
        return contactRepository.findOne(id);
    }

    @RequestMapping(value = "/api/users", method = RequestMethod.DELETE)
    @ResponseBody
    public void deletecontact(Integer id) {
        contactRepository.delete(id);
    }

    @RequestMapping(value = "/api/users", method = RequestMethod.POST)
    @ResponseBody
    public Contact createcontact(@RequestBody Contact contact) {
        return contactRepository.save(contact);
    }

    @RequestMapping(value = "/api/users/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public Contact updatecontact(@RequestBody Contact contact) {
        return contactRepository.save(contact);
    }
}
