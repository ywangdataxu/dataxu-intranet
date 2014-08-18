package dataxu.intranet.controller;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import dataxu.intranet.entity.ContactSchedule;
import dataxu.intranet.entity.Plan;
import dataxu.intranet.entity.PlanContact;
import dataxu.intranet.repository.ContactScheduleRepository;
import dataxu.intranet.repository.PlanContactRepository;
import dataxu.intranet.repository.PlanRepository;

@Controller
public class PlanController {
    @Autowired
    private PlanRepository planRepository;
    @Autowired
    private ContactScheduleRepository contactScheduleRepository;
    @Autowired
    private PlanContactRepository planContactRepository;

    @Autowired
    @RequestMapping(value = "/plans", method = RequestMethod.GET)
    public String plans() {
        return "plans";
    }

    @RequestMapping(value = "/api/plans", method = RequestMethod.GET)
    @ResponseBody
    public List<Plan> getPlans() {
        List<Plan> plans = planRepository.findAll();
        return plans;
    }

    @RequestMapping(value = "/api/plans/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Plan getPlan(@PathVariable("id") Integer id) {
        return planRepository.findOne(id);
    }

    @RequestMapping(value = "/api/plans", method = RequestMethod.DELETE)
    @ResponseBody
    public void deletePlan(Integer id) {
        planRepository.delete(id);
    }

    @RequestMapping(value = "/api/plans", method = RequestMethod.POST)
    @ResponseBody
    public Plan createPlan(@RequestBody Plan plan) {
        return planRepository.save(plan);
    }

    @RequestMapping(value = "/api/plans/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public Plan updatePlan(@RequestBody Plan plan) {
        List<PlanContact> planContacts = planContactRepository.findAllByPlanId(plan.getId());
        Map<Integer, PlanContact> toDelete = Maps.newHashMap();
        for (PlanContact pc : planContacts) {
            toDelete.put(pc.getId(), pc);
        }

        for (PlanContact pc : plan.getPlanContacts()) {
            pc.setPlanId(plan.getId());
            toDelete.remove(pc.getId());
        }

        planContactRepository.deleteInBatch(toDelete.values());

        return planRepository.save(plan);
    }

    @RequestMapping(value = "/api/plans/{id}/schedules", method = RequestMethod.GET)
    @ResponseBody
    public Map<Integer, List<ContactSchedule>> getPlanSchedules(@PathVariable("id") Integer planId) {
        Plan p = planRepository.findOne(planId);
        Set<PlanContact> contacts = p.getPlanContacts();

        Date planStartDate = p.getStartDate();
        Date planEndDate = p.getEndDate();

        Map<Integer, List<ContactSchedule>> schedules = Maps.newHashMap();
        for (PlanContact c : contacts) {
            List<ContactSchedule> s = contactScheduleRepository.findByContactId(c.getId());
            List<ContactSchedule> filtered = Lists.newArrayList();

            for (ContactSchedule cs : s) {
                Date scheduleStartDate = cs.getStartDate();
                Date scheduleEndDate = cs.getEndDate();

                if (scheduleEndDate.before(planStartDate) || scheduleStartDate.after(planEndDate)) {
                    continue;
                }

                Date newStartDate = scheduleStartDate;
                Date newEndDate = scheduleEndDate;

                if (scheduleStartDate.before(planStartDate)) {
                    newStartDate = planStartDate;
                }
                if (scheduleEndDate.after(planEndDate)) {
                    newEndDate = planEndDate;
                }

                ContactSchedule newSchedule = new ContactSchedule(cs, newStartDate, newEndDate);
                filtered.add(newSchedule);
            }
            Collections.sort(filtered);
            schedules.put(c.getId(), filtered);
        }

        return schedules;
    }

}
