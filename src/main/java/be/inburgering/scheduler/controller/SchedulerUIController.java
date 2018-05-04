package be.inburgering.scheduler.controller;

import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import be.inburgering.scheduler.domain.JobDetails;
import be.inburgering.scheduler.jobs.JobViaREST;

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
			JobViaREST.build(scheduler, job);
			return "redirect:/jobs";
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:/add?error";
		}
    }
    
    @RequestMapping(method = RequestMethod.GET, value = {"/edit/{name}"})
    public String editJob(Model model, @PathVariable String name) throws SchedulerException {
    	try {
	    	for (String groupName : scheduler.getJobGroupNames()) {
	            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
	            	if(jobKey.getName().equals(name)){
	            		JobDataMap dataMap = scheduler.getJobDetail(jobKey).getJobDataMap();
	            		CronTrigger trigger = (CronTrigger) scheduler.getTriggersOfJob(jobKey).get(0);
	            		
	            		model.addAttribute(JobViaREST.PARAM_SERVICE, dataMap.get(JobViaREST.PARAM_SERVICE));
	            		model.addAttribute(JobViaREST.PARAM_METHOD, dataMap.get(JobViaREST.PARAM_METHOD));
	            		model.addAttribute("cron", trigger.getCronExpression());
	            		model.addAttribute("name", name);
	            	}
	            }
	        }
	        
	    	return "edit.html";
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	return "redirect:/jobs";
		}
    }
    
    @RequestMapping(method = RequestMethod.POST, value = {"/edit/{name}"})
    public String postEditJob(JobDetails job, @PathVariable String name) throws SchedulerException {
    	try {
	    	for (String groupName : scheduler.getJobGroupNames()) {
	            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
	            	if(jobKey.getName().equals(name)){
	            		scheduler.deleteJob(jobKey);
	            	}
	            }
	        }
	    	
	    	JobViaREST.build(scheduler, job);
	    	
	        return "redirect:/jobs";
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	return "redirect:/edit?error";
		}
    }
}
