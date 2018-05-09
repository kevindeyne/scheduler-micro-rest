package be.inburgering.scheduler.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;

import be.inburgering.scheduler.domain.ScheduledJob;

public enum JobUtils {

    INSTANCE;
	
	private static final ScheduledJob DUMMY = new ScheduledJob(null, null, "No such job found", null, null);
	
	/**
	 * Lists the current running jobs as formatted {@link ScheduledJob} objects
	 * @param scheduler
	 * @return {@link ArrayList} of {@link ScheduledJob} objects
	 */
	public static List<ScheduledJob> listJobs(Scheduler scheduler) {
		List<ScheduledJob> scheduledJobs = new ArrayList<>();
        for (String groupName : getGroups(scheduler)) {
            for (JobKey jobKey : getJobsInGroup(scheduler, groupName)) {
            	Trigger trigger = findTrigger(scheduler, jobKey);
                ScheduledJob job = buildScheduledJob(scheduler, trigger);
				scheduledJobs.add(job);
            }
        }
		return scheduledJobs;
	}
	
	/**
	 * Lists the job as formatted {@link ScheduledJob} objects based on the name
	 * @param scheduler
	 * @return {@link ScheduledJob} object or null
	 */
	public static ScheduledJob listJob(Scheduler scheduler, String name) {
		JobKey jobKey = findJobKey(scheduler, name);
		Trigger trigger = findTrigger(scheduler, jobKey);    	
        return buildScheduledJob(scheduler, trigger);
	}
    
	/**
	 * Finds a {@link JobKey} object based on the name
	 * @param scheduler
	 * @param name
	 * @return {@link JobKey} or null
	 */
    public static JobKey findJobKey(Scheduler scheduler, String name) {
    	try {
	    	for (String groupName : getGroups(scheduler)) {
	            for (JobKey jobKey : getJobsInGroup(scheduler, groupName)) {
	            	if(jobKey.getName().equals(name)){
	            		return jobKey;
	            	}
	            }
	        }
	    } catch (Exception e) {
	    	e.printStackTrace();
		}
    	
    	return null;
    }
    
    /**
     * Find the {@link Trigger} object for the corresponding {@link JobKey}
     * @param scheduler
     * @param jobkey
     * @return {@link Trigger} or null
     */
    public static Trigger findTrigger(Scheduler scheduler, JobKey jobkey) {
    	if(jobkey != null) {
    		try {
    			return scheduler.getTriggersOfJob(jobkey).get(0);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}    		
    	}

    	return null;
    }
    
    /**
     * Find the {@link TriggerKey} for the corresponding {@link JobKey}
     * @param scheduler
     * @param jobkey
     * @return {@link TriggerKey} or null
     */
    public static TriggerKey findTriggerKey(Scheduler scheduler, JobKey jobkey) {
    	if(jobkey != null) {
    		try {
    			return findTrigger(scheduler, jobkey).getKey();
    		} catch (Exception e) {
    			e.printStackTrace();
    		}    		
    	}

    	return null;
    }
    
	private static ScheduledJob buildScheduledJob(Scheduler scheduler, Trigger trigger) {
		if(trigger == null) return DUMMY;
		
		TriggerState triggerState = getTriggerState(scheduler, trigger);
		JobKey jobKey = trigger.getJobKey();
		
		String state = triggerState.name();
		String jobName = jobKey.getName();
		String groupName = jobKey.getGroup();
		
		Date lastFireTime = trigger.getPreviousFireTime();
		Date nextFireTime = trigger.getNextFireTime();
				
		return new ScheduledJob(lastFireTime, nextFireTime, jobName, groupName, state);
	}

	private static TriggerState getTriggerState(Scheduler scheduler, Trigger trigger) {
		if(null != trigger) {			
			try {
				return scheduler.getTriggerState(trigger.getKey());
			} catch (SchedulerException e) {
				e.printStackTrace();
			}	
		}
		
		return TriggerState.ERROR;
	}
    
	private static Set<JobKey> getJobsInGroup(Scheduler scheduler, String groupName) {
		try {
			return scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName));
		} catch (SchedulerException e) {
			e.printStackTrace();
			return new HashSet<JobKey>();
		}
	}

	private static List<String> getGroups(Scheduler scheduler) {
		try {
			return scheduler.getJobGroupNames();
		} catch (SchedulerException e) {
			e.printStackTrace();
			return new ArrayList<String>();
		}
	}

}
