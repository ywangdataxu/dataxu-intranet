package dataxu.intranet.controller;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import dataxu.intranet.entity.ContactSchedule;
import dataxu.intranet.repository.ContactScheduleRepository;

@Controller
public class ScheduleController {
    @Autowired
    private ContactScheduleRepository contactScheduleRepository;

    @RequestMapping(value = "/api/users/{id}/schedules", method = RequestMethod.GET)
    @ResponseBody
    public List<ContactSchedule> getSchedules(@PathVariable("id") Integer contactId) {
        List<ContactSchedule> result = contactScheduleRepository.findByContactId(contactId);
        Collections.sort(result);

        return result;
    }

    @RequestMapping(value = "/api/users/{id}/schedules", method = RequestMethod.POST)
    @ResponseBody
    public List<ContactSchedule> updateSchedules(@PathVariable("id") Integer contactId,
            @RequestBody List<ContactSchedule> schedules) {
        List<ContactSchedule> existing = contactScheduleRepository.findByContactId(contactId);
        Set<Integer> ids = Sets.newHashSet();
        for (ContactSchedule s : schedules) {
            ids.add(s.getId());
        }

        List<ContactSchedule> toDelete = Lists.newArrayList();
        for (ContactSchedule e : existing) {
            if (!ids.contains(e.getId())) {
                toDelete.add(e);
            }
        }
        if (!toDelete.isEmpty()) {
            contactScheduleRepository.deleteInBatch(toDelete);
        }

        List<ContactSchedule> result = contactScheduleRepository.save(schedules);

        return result;
    }
}