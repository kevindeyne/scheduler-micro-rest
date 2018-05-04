package be.inburgering.scheduler.jobs;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

public class Sampler {

    public static void sampleJob(Scheduler scheduler) throws SchedulerException {
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
    
    public static void sampleRemoteJob(Scheduler scheduler) throws SchedulerException {
        Trigger trigger = TriggerBuilder.newTrigger().startNow().withSchedule(
                SimpleScheduleBuilder
                        .simpleSchedule()
                        .withIntervalInSeconds(10)
                        .repeatForever()).build();
        
        //TriggerBuilder.newTrigger().startNow().withSchedule(CronScheduleBuilder.cronSchedule("cron"));

        JobDetail job = JobBuilder.newJob(JobViaREST.class)
        		.usingJobData(JobViaREST.PARAM_SERVICE, "HvNCursusBeheerTaskService")
        		.usingJobData(JobViaREST.PARAM_METHOD, "processHerberekeningen")
                .withIdentity("testRemoteJob", "testGroup")
                .build();

        scheduler.scheduleJob(job, trigger);
    }
}
