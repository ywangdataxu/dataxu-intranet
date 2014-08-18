package dataxu.intranet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dataxu.intranet.entity.ContactSchedule;

public interface ContactScheduleRepository extends JpaRepository<ContactSchedule, Integer> {
    List<ContactSchedule> findByContactId(Integer contactId);
}
