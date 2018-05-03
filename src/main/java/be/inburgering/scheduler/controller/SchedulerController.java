package be.inburgering.scheduler.controller;

import be.inburgering.scheduler.domain.ScheduledJob;
import be.inburgering.scheduler.domain.SchedulerStatus;
import be.inburgering.scheduler.jobs.TestJob;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class SchedulerController {

    private Scheduler scheduler;

    private SchedulerController(Scheduler scheduler) throws Exception {
        this.scheduler = scheduler;

        sampleJob(scheduler);
    }

    private void sampleJob(Scheduler scheduler) throws SchedulerException {
        Trigger trigger = TriggerBuilder.newTrigger().startNow().withSchedule(
                SimpleScheduleBuilder
                        .simpleSchedule()
                        .withIntervalInSeconds(1)
                        .repeatForever()).build();

        JobDetail job = JobBuilder.newJob(TestJob.class)
                .withIdentity("testJob", "testGroup")
                .build();

        scheduler.scheduleJob(job, trigger);
    }

    @RequestMapping({"/", "/status"})
    public SchedulerStatus getSchedulerStatus() {
        return new SchedulerStatus(scheduler);
    }

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

    @RequestMapping("/add/")
    public List<ScheduledJob> addJob() {
        return new ArrayList<ScheduledJob>();
    }

}
