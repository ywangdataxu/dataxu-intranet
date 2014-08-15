package dataxu.intranet.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import dataxu.intranet.util.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Repository
class JsonClientRepository implements ClientRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ObjectMapper objectMapper;

    @Value("file:data/clients.json")
    private Resource clientsData;

    private ImmutableList<Client> clients;

    private List<Recommendation> recommendations;

    @Override
    public Client load(UUID id) {
        return clients.stream().filter(client -> client.getId().equals(id)).findFirst().orElseThrow(() -> new ResourceNotFoundException(id));
    }

    @Override
    public List<Client> findAll() {
        return clients;
    }

    @Override
    public Client save(Client client) {
        Client existing = load(client.getId());
        existing.updateFrom(client);
        return existing;
    }

    @PostConstruct
    void loadClients() {
        try (InputStream jsonStream = clientsData.getInputStream()) {
            clients = ImmutableList.copyOf(objectMapper.readValue(jsonStream, Client[].class));
        } catch (IOException e) {
            logger.error("error reading clients data from resource: " + clientsData, e);
            clients = ImmutableList.of();
        }

        logger.debug("loaded clients: {}", clients);
    }
}
