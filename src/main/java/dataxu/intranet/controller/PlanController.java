package dataxu.intranet.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import dataxu.intranet.entity.Plan;
import dataxu.intranet.repository.PlanRepository;

@Controller
public class PlanController {
    @Autowired
    private PlanRepository planRepository;

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
        return planRepository.save(plan);
    }
}
