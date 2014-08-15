package dataxu.intranet.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
        System.out.println("Got plans: " + plans);
        return plans;
    }
}
