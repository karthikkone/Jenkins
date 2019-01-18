package com.jenkinsjobs.Jobs;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class JobStatus {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long buildid;
	private String buildname;
	private String buildstatus;
	
	public JobStatus()
	{
		
	}
	public JobStatus(Long buildid, String buildname, String buildstatus) {
		super();
		this.buildid = buildid;
		this.buildname = buildname;
		this.buildstatus = buildstatus;
	}
	public Long getBuildid() {
		return buildid;
	}
	public void setBuildid(Long buildid) {
		this.buildid = buildid;
	}
	public String getBuildname() {
		return buildname;
	}
	public void setBuildname(String buildname) {
		this.buildname = buildname;
	}
	public String getBuildstatus() {
		return buildstatus;
	}
	public void setBuildstatus(String buildstatus) {
		this.buildstatus = buildstatus;
	}
	
	
	
}
