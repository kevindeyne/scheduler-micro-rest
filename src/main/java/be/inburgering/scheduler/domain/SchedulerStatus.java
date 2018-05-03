package be.inburgering.scheduler.domain;

import be.inburgering.scheduler.utils.DateToString;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;

import java.util.Date;
import java.util.List;

public class SchedulerStatus {

    private Date runningSince;
    private Date lastJobExecuted;
    private int threadPoolSize;
    private int jobsRunning;
    private int jobsExecutedSoFar;
    private State state;

    public SchedulerStatus(Scheduler scheduler){
        try{
            SchedulerMetaData metaData = scheduler.getMetaData();
            setLastJobExecuted(determineLastJobExecuted(scheduler));
            setJobsRunning(determineCurrentlyRunning(scheduler));
            setJobsExecutedSoFar(metaData.getNumberOfJobsExecuted());
            setRunningSince(metaData.getRunningSince());
            setThreadPoolSize(metaData.getThreadPoolSize());
            setStatus(metaData);
        } catch (SchedulerException e){
            setState(State.ERROR);
        }
    }

    private int determineCurrentlyRunning(Scheduler scheduler) throws SchedulerException {
        return scheduler.getCurrentlyExecutingJobs().size();
    }

    private Date determineLastJobExecuted(Scheduler scheduler) throws SchedulerException {
        Date lastFired = null;
        for (String groupName : scheduler.getJobGroupNames()) {
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
                Date lastFireTime = triggers.get(0).getPreviousFireTime();

                if(lastFired == null || lastFireTime.after(lastFired)) {
                    lastFired = lastFireTime;
                }
            }
        }
        return lastFired;
    }

    private void setStatus(SchedulerMetaData metaData) {
        if(metaData.isStarted()){
            setState(State.STARTED);
        } else if(metaData.isInStandbyMode()){
            setState(State.STANDBY);
        } else {
            setState(State.SHUTDOWN);
        }
    }

    public String getRunningSince() {
        return DateToString.INSTANCE.format(runningSince);
    }

    public void setRunningSince(Date runningSince) {
        this.runningSince = runningSince;
    }

    public String getLastJobExecuted() {
        return DateToString.INSTANCE.format(lastJobExecuted);
    }

    public void setLastJobExecuted(Date lastJobExecuted) {
        this.lastJobExecuted = lastJobExecuted;
    }

    public int getJobsRunning() {
        return jobsRunning;
    }

    public void setJobsRunning(int jobsRunning) {
        this.jobsRunning = jobsRunning;
    }

    public int getJobsExecutedSoFar() {
        return jobsExecutedSoFar;
    }

    public void setJobsExecutedSoFar(int jobsExecutedSoFar) {
        this.jobsExecutedSoFar = jobsExecutedSoFar;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    public void setThreadPoolSize(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }
}
