package dataxu.intranet.controller;

import dataxu.intranet.model.Client;
import dataxu.intranet.model.ClientRepository;
import dataxu.intranet.model.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/api/clients")
class ClientController {

    @Autowired
    private ClientRepository clientRepository;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    List<Client> getClients() {
        return clientRepository.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    Client getClient(@PathVariable String id) {
        return clientRepository.load(UUID.fromString(id));
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    ResponseEntity<Object> save(@RequestBody @Valid Client client, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(ValidationError.of(bindingResult), HttpStatus.BAD_REQUEST);
        }

        Client savedClient = clientRepository.save(client);
        return new ResponseEntity<Object>(savedClient, HttpStatus.OK);
    }
}
