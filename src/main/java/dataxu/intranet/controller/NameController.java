package dataxu.intranet.controller;

import dataxu.intranet.model.Client;
import dataxu.intranet.model.ClientRepository;
import dataxu.intranet.util.BadRequestException;
import dataxu.intranet.util.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api/names")
class NameController {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Pattern expectedPrefixPattern = Pattern.compile("[a-zA-z]+");
    @Autowired
    private ClientRepository clientRepository;

    @RequestMapping("/response-entity/{prefix}")
    ResponseEntity<Object> namesByPrefixWithResponseEntity(@PathVariable String prefix) {
        logger.info("received call to: /api/names/response-entity/{}", prefix);

        if (!expectedPrefixPattern.matcher(prefix).matches()) {
            return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
        }

        List<String> firstNames = new ArrayList<>();
        for (Client client : clientRepository.findAll()) {
            if (client.getFirstName().startsWith(prefix)) {
                firstNames.add(client.getFirstName());
            }
        }

        if (firstNames.isEmpty()) {
            return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<Object>(firstNames, HttpStatus.OK);
    }

    @RequestMapping("/exception-based/{prefix}")
    List<String> exceptionBasedNamesByPrefix(@PathVariable String prefix) {
        logger.info("received call to: /api/names/exception-based/{}", prefix);

        if (!expectedPrefixPattern.matcher(prefix).matches()) {
            throw new BadRequestException("prefix does not match pattern: " + expectedPrefixPattern.pattern());
        }

        List<String> firstNames = clientRepository.findAll().stream()
                .filter(c -> c.getFirstName().startsWith(prefix))
                .map(c -> c.getFirstName())
                .collect(toList());

        if (firstNames.isEmpty()) {
            throw new ResourceNotFoundException(prefix);
        }

        return firstNames;
    }
}
