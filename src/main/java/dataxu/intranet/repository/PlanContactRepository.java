package dataxu.intranet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dataxu.intranet.entity.PlanContact;

public interface PlanContactRepository extends JpaRepository<PlanContact, Integer> {
    List<PlanContact> findAllByPlanId(Integer planId);
}
