package dataxu.intranet.controller;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.time.FastDateFormat;
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

    public static class PlanSchedule {
        private Date date;
        private Double velocity;

        public PlanSchedule() {

        }

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

        public void setDate(Date date) {
            this.date = date;
        }

        public void setVelocity(Double velocity) {
            this.velocity = velocity;
        }
    }

    public static class ScheduleChartData {
        private final String chapterName;
        private final List<Double> data;

        public ScheduleChartData(String chapterName, List<Double> data) {
            this.data = data;
            this.chapterName = chapterName;
        }

        public String getChapterName() {
            return chapterName;
        }

        public List<Double> getData() {
            return data;
        }
    }

    public static class ScheduleChartDataSet {
        private final List<Date> dates;
        private final List<ScheduleChartData> dataSet;

        public ScheduleChartDataSet(List<Date> dates, List<ScheduleChartData> dataSet) {
            this.dates = dates;
            this.dataSet = dataSet;
        }

        public List<ScheduleChartData> getDataSet() {
            return dataSet;
        }

        public List<String> getDates() {
            Collections.sort(dates);
            List<String> result = Lists.newArrayList();
            for (Date d : dates) {
                result.add(DATE_FORMAT.format(d));
            }
            return result;
        }
    }

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private ContactScheduleRepository contactScheduleRepository;

    @Autowired
    private PlanContactRepository planContactRepository;

    private static final Map<Integer, String> CHAPTERS = Maps.newHashMap();

    static {
        CHAPTERS.put(1, "LS");
        CHAPTERS.put(2, "RTS");
        CHAPTERS.put(3, "RWH");
        CHAPTERS.put(4, "UI");
    }

    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("MM/dd/yyyy");

    private List<Double> accumulateList(Iterable<Double> src) {
        List<Double> result = Lists.newArrayList();

        double curr = 0;
        for (Double d : src) {
            curr += d;
            result.add(curr);
        }

        return result;
    }

    private Map<Date, Double> addTwoMaps(Map<Date, Double> d1, Map<Date, Double> d2) {
        Map<Date, Double> result = Maps.newTreeMap();

        for (Date d : d1.keySet()) {
            result.put(d, d1.get(d) + d2.get(d));
        }

        return result;
    }

    @RequestMapping(value = "/api/plans", method = RequestMethod.POST)
    @ResponseBody
    public Plan createPlan(@RequestBody Plan plan) {
        return planRepository.save(plan);
    }

    @RequestMapping(value = "/api/plans", method = RequestMethod.DELETE)
    @ResponseBody
    public void deletePlan(Integer id) {
        Plan p = planRepository.findOne(id);

        planRepository.delete(p);
    }

    private List<Date> getAllMondays(Iterable<Date> src) {
        Set<Date> result = Sets.newHashSet();
        for (Date d : src) {
            Date monday = getMonday(d);
            result.add(monday);
        }

        return Lists.newArrayList(result);
    }

    private Set<Date> getDaysInWeeks(Date startDate, Date endDate) {
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

    private Date getMonday(Date src) {
        Calendar srcDate = Calendar.getInstance();
        srcDate.setTime(src);
        srcDate.set(Calendar.DAY_OF_WEEK, 2);

        return srcDate.getTime();
    }

    @RequestMapping(value = "/api/plans/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Plan getPlan(@PathVariable("id") Integer id) {
        return planRepository.findOne(id);
    }

    @RequestMapping(value = "/api/plans", method = RequestMethod.GET)
    @ResponseBody
    public List<Plan> getPlans() {
        List<Plan> plans = planRepository.findAll();
        Collections.sort(plans);

        return plans;
    }

    @RequestMapping(value = "/api/plans/{id}/schedules", method = RequestMethod.GET)
    @ResponseBody
    public ScheduleChartDataSet getPlanSchedules(@PathVariable("id") Integer planId, String chartType) {
        Plan plan = planRepository.findOne(planId);
        List<PlanContact> contacts = plan.getPlanContacts();

        Map<Integer, Map<Date, Double>> chapterVelocities = Maps.newTreeMap();

        for (PlanContact c : contacts) {
            // determine the velocity
            Integer chapterId = c.getChapterId();

            Double velocity = 0D;
            List<ContactVelocity> velocities = c.getContact().getVelocities();
            for (ContactVelocity v : velocities) {
                if (v.getChapter().getId() == chapterId) {
                    velocity = v.getVelocity();
                    break;
                }
            }

            if (velocity == 0) {
                continue;
            }

            List<ContactSchedule> s = contactScheduleRepository.findByContactId(c.getContact().getId());
            List<ContactSchedule> filtered = Lists.newArrayList();

            for (ContactSchedule cs : s) {
                ContactSchedule newSchedule = updateScheduleBaseOnPlan(plan, cs);
                if (newSchedule != null) {
                    filtered.add(newSchedule);
                }
            }

            Map<Date, Double> contactVelocities = getVelocities(velocity, filtered, plan);

            boolean valid = false;
            for (Double d : contactVelocities.values()) {
                if (d > 0) {
                    valid = true;
                    break;
                }
            }

            if (valid) {
                if (!chapterVelocities.containsKey(chapterId)) {
                    chapterVelocities.put(chapterId, contactVelocities);
                } else {
                    // add to the existing one
                    Map<Date, Double> existing = chapterVelocities.get(chapterId);
                    Map<Date, Double> newList = addTwoMaps(contactVelocities, existing);
                    chapterVelocities.put(chapterId, newList);
                }
            }
        }

        // accumulated one
        List<Date> allWorkingDays = Lists.newArrayList(getDaysInWeeks(plan.getStartDate(), plan.getEndDate()));
        List<ScheduleChartData> dataSet = Lists.newArrayList();

        List<Date> resultWorkingDays = allWorkingDays;
        Map<Integer, Map<Date, Double>> resultChapterVelocities = chapterVelocities;

        boolean groupByWeek = true;
        if (groupByWeek) {
            resultWorkingDays = getAllMondays(allWorkingDays);

            resultChapterVelocities = Maps.newTreeMap();

            for (Map.Entry<Integer, Map<Date, Double>> entry : chapterVelocities.entrySet()) {
                resultChapterVelocities.put(entry.getKey(), groupByWeek(entry.getValue()));
            }
        }

        boolean accumulated = "accumulated".equals(chartType);
        for (Map.Entry<Integer, Map<Date, Double>> entry : resultChapterVelocities.entrySet()) {
            List<Double> data;
            if (accumulated) {
                data = accumulateList(entry.getValue().values());
            } else {
                data = Lists.newArrayList(entry.getValue().values());
            }

            List<Double> realData = Lists.newArrayList();
            // now divided by 10
            for (Double d : data) {
                realData.add(d / 10 * (1 - (double) plan.getMaintenanceAllowance() / 100));
            }

            dataSet.add(new ScheduleChartData(CHAPTERS.get(entry.getKey()), realData));
        }

        return new ScheduleChartDataSet(Lists.newArrayList(resultWorkingDays), dataSet);
    }

    private Map<Date, Double> getVelocities(double velocity, List<ContactSchedule> schedules, Plan plan) {
        Collections.sort(schedules);

        Date startDate = plan.getStartDate();
        Date endDate = plan.getEndDate();

        Set<Date> allWorkingDays = getDaysInWeeks(startDate, endDate);

        Map<Date, Double> result = Maps.newTreeMap();
        for (Date d : allWorkingDays) {
            result.put(d, velocity);
        }

        for (ContactSchedule schedule : schedules) {
            Set<Date> nonWorkingDays = getDaysInWeeks(schedule.getStartDate(), schedule.getEndDate());

            for (Date d : nonWorkingDays) {
                result.put(d, 0D);
            }
        }

        return result;
    }

    private Map<Date, Double> groupByWeek(Map<Date, Double> velocities) {
        Map<Date, Double> result = Maps.newTreeMap();

        for (Date d : velocities.keySet()) {
            Date monday = getMonday(d);
            double v = 0;
            if (result.containsKey(monday)) {
                v = result.get(monday);
            }

            result.put(monday, v + velocities.get(d));
        }

        return result;
    }

    @Autowired
    @RequestMapping(value = "/plans", method = RequestMethod.GET)
    public String plans() {
        return "plans";
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

    private ContactSchedule updateScheduleBaseOnPlan(Plan plan, ContactSchedule cs) {
        Date planStartDate = plan.getStartDate();
        Date planEndDate = plan.getEndDate();

        Date scheduleStartDate = cs.getStartDate();
        Date scheduleEndDate = cs.getEndDate();

        if (scheduleEndDate.before(planStartDate) || scheduleStartDate.after(planEndDate)) {
            return null;
        }

        Date newStartDate = scheduleStartDate;
        Date newEndDate = scheduleEndDate;

        if (scheduleStartDate.before(planStartDate)) {
            newStartDate = planStartDate;
        }
        if (scheduleEndDate.after(planEndDate)) {
            newEndDate = planEndDate;
        }

        return new ContactSchedule(cs, newStartDate, newEndDate);
    }
}
