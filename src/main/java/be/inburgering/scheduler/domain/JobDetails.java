package be.inburgering.scheduler.domain;

/**
 * UI Mapping class
 * @author KDNBG75
 */
public class JobDetails {

	private String service;
	private String method;
	private String cron;
	
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getCron() {
		return cron;
	}
	public void setCron(String cron) {
		this.cron = cron;
	}
	public String name() {
		return service + "-" + method;
	}
	public String group() {
		return service.replace("Service", "Group");
	}
}
