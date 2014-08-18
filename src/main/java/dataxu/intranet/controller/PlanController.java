package dataxu.intranet.controller;

import java.text.ParseException;
import java.util.Calendar;
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
import com.google.common.collect.Sets;

import dataxu.intranet.entity.ContactSchedule;
import dataxu.intranet.entity.ContactVelocity;
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
        Collections.sort(plans);

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

    public static class PlanSchedule {
        private final Date date;
        private final Double velocity;

        public PlanSchedule(Date date, Double velocity) {
            this.date = date;
            this.velocity = velocity;
        }

        public Date getDate() {
            return date;
        }

        public Double getVelocity() {
            return velocity;
        }
    }

    public static class ChapterSchedule {
        private final Integer chapterId;
        private final List<PlanSchedule> planSchedules;

        public ChapterSchedule(Integer chapterId, List<PlanSchedule> planSchedules) {
            this.chapterId = chapterId;
            this.planSchedules = planSchedules;
        }

        public Integer getChapterId() {
            return chapterId;
        }

        public List<PlanSchedule> getPlanSchedules() {
            return planSchedules;
        }

    }

    @RequestMapping(value = "/api/plans/{id}/schedules", method = RequestMethod.GET)
    @ResponseBody
    public List<ChapterSchedule> getPlanSchedules(@PathVariable("id") Integer planId) {
        Plan p = planRepository.findOne(planId);
        List<PlanContact> contacts = p.getPlanContacts();

        Date planStartDate = p.getStartDate();
        Date planEndDate = p.getEndDate();

        Map<Integer, Map<Date, Double>> schedules = Maps.newHashMap();
        for (PlanContact c : contacts) {
            List<ContactSchedule> s = contactScheduleRepository.findByContactId(c.getContact().getId());
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

            Integer chapterId = c.getChapterId();
            Integer velocity = 0;
            List<ContactVelocity> velocities = c.getContact().getVelocities();
            for (ContactVelocity v : velocities) {
                if (v.getChapter().getId() == chapterId) {
                    velocity = v.getVelocity();
                }
            }
            Map<Date, Double> currAccumulatedVelocity = getAccumulatedVelocity(velocity / 10.0, filtered,
                    planStartDate, planEndDate);
            if (schedules.containsKey(chapterId)) {
                Map<Date, Double> existing = schedules.get(chapterId);
                Map<Date, Double> newOne = Maps.newTreeMap();

                // merge
                for (Date d : existing.keySet()) {
                    newOne.put(d, existing.get(d) + currAccumulatedVelocity.get(d));
                }
                schedules.put(chapterId, newOne);
            } else {
                schedules.put(chapterId, currAccumulatedVelocity);
            }
        }

        List<ChapterSchedule> result = Lists.newArrayList();
        for (Integer i : schedules.keySet()) {
            List<PlanSchedule> ps = Lists.newArrayList();

            for (Date d : schedules.get(i).keySet()) {
                ps.add(new PlanSchedule(d, schedules.get(i).get(d)));
            }

            result.add(new ChapterSchedule(i, ps));
        }

        return result;
    }

    private static Map<Date, Double> getAccumulatedVelocity(double velocity, List<ContactSchedule> schedules,
            Date startDate, Date endDate) {
        Set<Date> workingDays = getWorkingDays(schedules, startDate, endDate);
        Set<Date> all = getDaysInWeeks(startDate, endDate);

        Map<Date, Double> result = Maps.newTreeMap();

        double accumulated = 0;
        for (Date a : all) {
            if (workingDays.contains(a)) {
                accumulated += velocity;
            }
            result.put(a, accumulated);
        }

        return result;
    }

    private static Set<Date> getWorkingDays(List<ContactSchedule> schedules, Date startDate, Date endDate) {
        Collections.sort(schedules);

        Set<Date> allDays = getDaysInWeeks(startDate, endDate);

        for (ContactSchedule schedule : schedules) {
            Set<Date> nonWorkingDays = getDaysInWeeks(schedule.getStartDate(), schedule.getEndDate());

            for (Date d : nonWorkingDays) {
                allDays.remove(d);
            }
        }

        return allDays;
    }

    public static void main(String[] args) throws ParseException {
        // FastDateFormat format = FastDateFormat.getInstance("MM/dd/yyyy");
        //
        // Date start = format.parse("08/18/2014");
        // Date end = format.parse("08/26/2014");
        //
        // ContactSchedule cs = new ContactSchedule();
        // cs.setStartDate(format.parse("08/19/2014"));
        // cs.setEndDate(format.parse("08/20/2014"));
        //
        // List<ContactSchedule> schedules = Lists.newArrayList(cs);
        //
        // List<Double> result = getAccumulatedVelocity(0.6, schedules, start,
        // end);
        //
        // for (Double d : result) {
        // System.out.println(d);
        // }
    }

    private static Set<Date> getDaysInWeeks(Date startDate, Date endDate) {
        Calendar start = Calendar.getInstance();
        start.setTime(startDate);

        Calendar end = Calendar.getInstance();
        end.setTime(endDate);

        Set<Date> result = Sets.newTreeSet();
        for (Calendar s = start; !start.after(end);) {
            int currDate = s.get(Calendar.DAY_OF_WEEK);
            if (currDate >= 2 && currDate <= 6) {
                result.add(new Date(s.getTime().getTime()));
            }
            s.add(Calendar.DATE, 1);
        }

        return result;
    }
}
