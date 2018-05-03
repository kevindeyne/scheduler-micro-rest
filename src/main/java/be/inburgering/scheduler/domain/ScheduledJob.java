package be.inburgering.scheduler.domain;

import be.inburgering.scheduler.utils.DateToString;

import java.util.Date;

public class ScheduledJob {

    private Date lastRun;
    private Date nextRun;
    private String name;
    private String group;

    public ScheduledJob(Date lastRun, Date nextRun, String name, String group) {
        this.lastRun = lastRun;
        this.nextRun = nextRun;
        this.name = name;
        this.group = group;
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
}
