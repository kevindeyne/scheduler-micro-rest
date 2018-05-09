package be.inburgering.scheduler.domain;

import be.inburgering.scheduler.utils.DateToString;

import java.util.Date;

/**
 * JSON REST mapping class
 * @author KDNBG75
 */
public class ScheduledJob {

	private String name;
    private String group;
    private String status;
    private Date lastRun;
    private Date nextRun;
    
    public ScheduledJob(Date lastRun, Date nextRun, String name, String group, String status) {
        this.lastRun = lastRun;
        this.nextRun = nextRun;
        this.name = name;
        this.group = group;
        this.setStatus(status);
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
