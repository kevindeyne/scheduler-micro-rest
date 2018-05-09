package be.inburgering.scheduler.controller;


import static be.inburgering.scheduler.jobs.JobViaREST.CRON;
import static be.inburgering.scheduler.jobs.JobViaREST.NAME;
import static be.inburgering.scheduler.jobs.JobViaREST.PARAM_METHOD;
import static be.inburgering.scheduler.jobs.JobViaREST.PARAM_SERVICE;
import static be.inburgering.scheduler.jobs.JobViaREST.build;
import static be.inburgering.scheduler.utils.JobUtils.findJobKey;

import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import be.inburgering.scheduler.domain.JobDetails;

@Controller
public class SchedulerUIController {

    private Scheduler scheduler;

    private SchedulerUIController(Scheduler scheduler) throws Exception {
        this.scheduler = scheduler;
    }

    @RequestMapping(method = RequestMethod.GET, value = {"/add"})
    public String addJob() {
        return "add.html";
    }
    
    @RequestMapping(method = RequestMethod.POST, value = {"/add"})
    public String postAddJob(JobDetails job) {
    	try {
			build(scheduler, job);
			return "redirect:/jobs";
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:/add?error";
		}
    }
    
    @RequestMapping(method = RequestMethod.GET, value = {"/edit/{name}"})
    public String editJob(Model model, @PathVariable String name) throws SchedulerException {
    	try {
	    	JobKey jobKey = findJobKey(scheduler, name);
	    	
	    	JobDataMap dataMap = scheduler.getJobDetail(jobKey).getJobDataMap();
    		CronTrigger trigger = (CronTrigger) scheduler.getTriggersOfJob(jobKey).get(0);
    		
    		model.addAttribute(PARAM_SERVICE, dataMap.get(PARAM_SERVICE));
    		model.addAttribute(PARAM_METHOD, dataMap.get(PARAM_METHOD));
    		model.addAttribute(CRON, trigger.getCronExpression());
    		model.addAttribute(NAME, name);
	        
	    	return "edit.html";
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	return "redirect:/jobs";
		}
    }
    
    @RequestMapping(method = RequestMethod.POST, value = {"/edit/{name}"})
    public String postEditJob(JobDetails job, @PathVariable String name) throws SchedulerException {
    	try {
	    	JobKey jobKey = findJobKey(scheduler, name);
	    	scheduler.deleteJob(jobKey);
	    	
	    	build(scheduler, job);
	    	
	        return "redirect:/jobs";
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	return "redirect:/edit?error";
		}
    }
}
