package be.inburgering.scheduler.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;

import be.inburgering.scheduler.domain.ScheduledJob;
import be.inburgering.scheduler.domain.State;

public enum JobUtils {

    INSTANCE;
	
	private static final ScheduledJob DUMMY = new ScheduledJob(null, null, "No such job found", null);
	
	public Map<String, State> lastExecutionState = new HashMap<String, State>();
	
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
    
    /**
     * Sets the last execution status of a job based on its context
     * @param context
     * @param state object of {@link State}
     */
	public static void setLastExecutionStatus(JobExecutionContext context, State state) {
		String key = getJobStatusKey(context.getJobDetail());
		if(key != null) {
			INSTANCE.lastExecutionState.put(key, state);
		}
	}
    
    /**
     * Returns a key for map usage based on group and jobname
     * @param group
     * @param jobName
     * @return jobName#group
     */
    public static String getJobStatusKey(String group, String jobName) {
    	return jobName + "#" + group;
    }
    
    /**
     * Returns a key for map usage based on {@link JobDetail}
     * @param jobDetail
     * @return jobName#group or null
     */
    private static String getJobStatusKey(JobDetail jobDetail) {
    	JobKey key = jobDetail.getKey();
		if(key == null) return null;
    	return getJobStatusKey(key.getGroup(), key.getName());
    }
    
	private static ScheduledJob buildScheduledJob(Scheduler scheduler, Trigger trigger) {
		if(trigger == null) return DUMMY;
		
		JobKey jobKey = trigger.getJobKey();
		
		String jobName = jobKey.getName();
		String groupName = jobKey.getGroup();
		
		Date lastFireTime = trigger.getPreviousFireTime();
		Date nextFireTime = trigger.getNextFireTime();
				
		return new ScheduledJob(lastFireTime, nextFireTime, jobName, groupName);
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
