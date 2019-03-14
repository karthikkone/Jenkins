package com.jenkinsjobs.model;

public class JobConfiguration {
	private String jobName;
	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	private String description;
	private String githubProject;
	private String gitBranch;
	private String githubCredentialId;
	private String targetOrgCredentialId;
	private String buildTargets;
	private String batchScript;
	
	public JobConfiguration() {
		super();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getGithubProject() {
		return githubProject;
	}

	public void setGithubProject(String githubProject) {
		this.githubProject = githubProject;
	}

	public String getGitBranch() {
		return gitBranch;
	}

	public void setGitBranch(String gitBranch) {
		this.gitBranch = gitBranch;
	}

	public String getGithubCredentialId() {
		return githubCredentialId;
	}

	public void setGithubCredentialId(String githubCredentialId) {
		this.githubCredentialId = githubCredentialId;
	}

	public String getTargetOrgCredentialId() {
		return targetOrgCredentialId;
	}

	public void setTargetOrgCredentialId(String targetOrgCredentialId) {
		this.targetOrgCredentialId = targetOrgCredentialId;
	}

	public String getBuildTargets() {
		return buildTargets;
	}

	public void setBuildTargets(String buildTargets) {
		this.buildTargets = buildTargets;
	}

	public String getBatchScript() {
		return batchScript;
	}

	public void setBatchScript(String batchScript) {
		this.batchScript = batchScript;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((batchScript == null) ? 0 : batchScript.hashCode());
		result = prime * result + ((buildTargets == null) ? 0 : buildTargets.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((gitBranch == null) ? 0 : gitBranch.hashCode());
		result = prime * result + ((githubCredentialId == null) ? 0 : githubCredentialId.hashCode());
		result = prime * result + ((githubProject == null) ? 0 : githubProject.hashCode());
		result = prime * result + ((jobName == null) ? 0 : jobName.hashCode());
		result = prime * result + ((targetOrgCredentialId == null) ? 0 : targetOrgCredentialId.hashCode());
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
		JobConfiguration other = (JobConfiguration) obj;
		if (batchScript == null) {
			if (other.batchScript != null)
				return false;
		} else if (!batchScript.equals(other.batchScript))
			return false;
		if (buildTargets == null) {
			if (other.buildTargets != null)
				return false;
		} else if (!buildTargets.equals(other.buildTargets))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (gitBranch == null) {
			if (other.gitBranch != null)
				return false;
		} else if (!gitBranch.equals(other.gitBranch))
			return false;
		if (githubCredentialId == null) {
			if (other.githubCredentialId != null)
				return false;
		} else if (!githubCredentialId.equals(other.githubCredentialId))
			return false;
		if (githubProject == null) {
			if (other.githubProject != null)
				return false;
		} else if (!githubProject.equals(other.githubProject))
			return false;
		if (jobName == null) {
			if (other.jobName != null)
				return false;
		} else if (!jobName.equals(other.jobName))
			return false;
		if (targetOrgCredentialId == null) {
			if (other.targetOrgCredentialId != null)
				return false;
		} else if (!targetOrgCredentialId.equals(other.targetOrgCredentialId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "JobConfiguration [jobName=" + jobName + ", description=" + description + ", githubProject="
				+ githubProject + ", gitBranch=" + gitBranch + ", githubCredentialId=" + githubCredentialId
				+ ", targetOrgCredentialId=" + targetOrgCredentialId + ", buildTargets=" + buildTargets
				+ ", batchScript=" + batchScript + "]";
	}
	
	
	
} 
