package dataxu.intranet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dataxu.intranet.entity.Contact;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
}
