package com.jenkinsjobs.model;

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
	public JobStatus(String buildname, String buildstatus) {
		super();
		//this.buildid = buildid;
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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((buildid == null) ? 0 : buildid.hashCode());
		result = prime * result + ((buildname == null) ? 0 : buildname.hashCode());
		result = prime * result + ((buildstatus == null) ? 0 : buildstatus.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JobStatus other = (JobStatus) obj;
		if (buildid == null) {
			if (other.buildid != null)
				return false;
		} else if (!buildid.equals(other.buildid))
			return false;
		if (buildname == null) {
			if (other.buildname != null)
				return false;
		} else if (!buildname.equals(other.buildname))
			return false;
		if (buildstatus == null) {
			if (other.buildstatus != null)
				return false;
		} else if (!buildstatus.equals(other.buildstatus))
			return false;
		return true;
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
