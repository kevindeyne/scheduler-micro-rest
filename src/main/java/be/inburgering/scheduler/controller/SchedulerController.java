package be.inburgering.scheduler.controller;

import static be.inburgering.scheduler.utils.JobUtils.findJobKey;
import static be.inburgering.scheduler.utils.JobUtils.findTriggerKey;
import static be.inburgering.scheduler.utils.JobUtils.listJob;
import static be.inburgering.scheduler.utils.JobUtils.listJobs;

import java.util.List;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import be.inburgering.scheduler.domain.ScheduledJob;
import be.inburgering.scheduler.domain.SchedulerStatus;

@RestController
public class SchedulerController {

    private Scheduler scheduler;

    private SchedulerController(Scheduler scheduler) throws Exception {
        this.scheduler = scheduler;

        //Sampler.sampleJob(scheduler);
        //Sampler.sampleRemoteJob(scheduler);
    }

    @RequestMapping({"/", "/status"})
    public SchedulerStatus getSchedulerStatus() {
        return new SchedulerStatus(scheduler);
    }

	@RequestMapping("/jobs")
    public List<ScheduledJob> getJobs() throws Exception {
        return listJobs(scheduler);
    }
    
    @RequestMapping("/pause/{name}")
    public ScheduledJob pauseJob(@PathVariable String name) throws Exception {
    	JobKey jobKey = findJobKey(scheduler, name);
    	scheduler.pauseJob(jobKey);
		scheduler.pauseTrigger(findTriggerKey(scheduler, jobKey));

    	return listJob(scheduler, name);
    }
    
    @RequestMapping("/resume/{name}")
    public ScheduledJob resumeJob(@PathVariable String name) throws Exception {
    	JobKey jobKey = findJobKey(scheduler, name);
    	scheduler.resumeJob(jobKey);
		scheduler.resumeTrigger(findTriggerKey(scheduler, jobKey));
    	
		return listJob(scheduler, name);
    }
    
    @RequestMapping("/delete/{name}")
    public List<ScheduledJob> deleteJob(@PathVariable String name) throws Exception {
    	JobKey jobKey = findJobKey(scheduler, name);
    	scheduler.deleteJob(jobKey);

    	return getJobs();
    }
}