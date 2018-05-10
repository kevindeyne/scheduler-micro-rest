package be.inburgering.scheduler.domain;

import static be.inburgering.scheduler.utils.JobUtils.INSTANCE;

import java.util.Date;

import be.inburgering.scheduler.utils.DateToString;
import be.inburgering.scheduler.utils.JobUtils;

/**
 * JSON REST mapping class
 * @author KDNBG75
 */
public class ScheduledJob {
	
	private static final String NOT_YET_TRIGGERED = "NOT YET TRIGGERED";

	private String name;
    private String group;
    private String status;
    private Date lastRun;
    private Date nextRun;
    
    public ScheduledJob(Date lastRun, Date nextRun, String name, String group) {
        this.lastRun = lastRun;
        this.nextRun = nextRun;
        this.name = name;
        this.group = group;
        this.status = getStatus(name, group);
    }

	private String getStatus(String name, String group) {
		String key = JobUtils.getJobStatusKey(group, name);
		
		State lastState = null;
		if(key != null){
			lastState = INSTANCE.lastExecutionState.get(key);
			if(null == lastState){
				return NOT_YET_TRIGGERED;
			}
		} else {
			lastState = State.INVALID;
		}
		
		return lastState.name();
	}

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getLastRun() {
        return DateToString.INSTANCE.format(lastRun);
    }

    public void setLastRun(Date lastRun) {
        this.lastRun = lastRun;
    }

    public String getNextRun() {
        return DateToString.INSTANCE.format(nextRun);
    }

    public void setNextRun(Date nextRun) {
        this.nextRun = nextRun;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
