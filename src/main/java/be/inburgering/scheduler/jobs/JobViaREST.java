package be.inburgering.scheduler.jobs;

import static be.inburgering.scheduler.utils.JobUtils.setLastExecutionStatus;

import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.web.client.RestTemplate;

import be.inburgering.scheduler.domain.JobDetails;
import be.inburgering.scheduler.domain.RestResult;
import be.inburgering.scheduler.domain.State;

public class JobViaREST implements Job {
	
	private static final String REMOTE_CALL_LOCATION = "http://localhost/inburgering/remote-call/%1$s/%2$s";
	
	private final boolean retryImmediately = false; //also see misfire config later on
	
	public static final String PARAM_SERVICE = "service";
	public static final String PARAM_METHOD = "method";
	public static final String CRON = "cron";
	public static final String NAME = "name";
	private static final String _OK = "ok";
	
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
    	String service = getJobData(context, PARAM_SERVICE);
    	String method = getJobData(context, PARAM_METHOD);
    	
  	    try{
	    	RestResult result = new RestTemplate()
	    			.getForObject(
	    					String.format(REMOTE_CALL_LOCATION, service, method), 
	    					RestResult.class);
	    	
	        if(result == null || !result.getStatus().equals(_OK)){
	        	setLastExecutionStatus(context, State.ERROR);	        	
	        	throw jobException(null, service, method);
	        } else {
	        	setLastExecutionStatus(context, State.OK);
	        }
  	    }catch(Throwable e){
  	    	setLastExecutionStatus(context, State.ERROR);
  	    	throw jobException(e, service, method);
	    }
    }

	private String getJobData(JobExecutionContext context, String param) {
		return context.getMergedJobDataMap().get(param).toString();
	}

	private JobExecutionException jobException(Throwable e, String service, String method) {
		if(null == e) {
			return new JobExecutionException(String.format("Failed REST call to %1$s#%2$s", service, method), retryImmediately);
		} else {
			return new JobExecutionException(String.format("Error in REST call to %1$s#%2$s", service, method), e, retryImmediately);	
		}
	}
	
    public static void build(Scheduler scheduler, JobDetails job) throws SchedulerException {
        Trigger trigger = TriggerBuilder.newTrigger().startNow()
        		.withSchedule(CronScheduleBuilder
        				.cronSchedule(job.getCron())
        				.withMisfireHandlingInstructionDoNothing()) //we don't really want to keep pushing a crashing server with constant misfires, most jobs should repeat anyway, so its just for the next cycle
        		.build();

        JobDetail newJob = JobBuilder.newJob(JobViaREST.class)
        		.usingJobData(JobViaREST.PARAM_SERVICE, job.getService())
        		.usingJobData(JobViaREST.PARAM_METHOD, job.getMethod())
                .withIdentity(job.name(), job.group())
                .build();

        scheduler.scheduleJob(newJob, trigger);
    }
}