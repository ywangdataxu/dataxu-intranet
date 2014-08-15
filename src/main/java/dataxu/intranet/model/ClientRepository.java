package dataxu.intranet.model;

import java.util.List;
import java.util.UUID;

public interface ClientRepository {
    public Client load(UUID id);

    public List<Client> findAll();

    public Client save(Client client);
}
