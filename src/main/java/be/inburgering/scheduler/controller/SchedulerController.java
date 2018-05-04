package be.inburgering.scheduler.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import be.inburgering.scheduler.domain.ScheduledJob;
import be.inburgering.scheduler.domain.SchedulerStatus;
import be.inburgering.scheduler.jobs.Sampler;

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

    @SuppressWarnings("unchecked")
	@RequestMapping("/jobs")
    public List<ScheduledJob> getJobs() throws Exception {
        List<ScheduledJob> scheduledJobs = new ArrayList<>();
        for (String groupName : scheduler.getJobGroupNames()) {
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
                Date lastFireTime = triggers.get(0).getPreviousFireTime();
                Date nextFireTime = triggers.get(0).getNextFireTime();
                scheduledJobs.add(new ScheduledJob(lastFireTime, nextFireTime, jobKey.getName(), groupName));
            }
        }

        return scheduledJobs;
    }

    @RequestMapping("/add-job")
    public List<ScheduledJob> addJob() {
        return new ArrayList<ScheduledJob>();
    }
    
    @RequestMapping("/edit-job/{name}")
    public List<ScheduledJob> addJob(@PathVariable String name) {
        return new ArrayList<ScheduledJob>();
    }

}
