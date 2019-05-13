package com.jenkinsjobs.Jobs;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;

import com.jenkinsjobs.model.JobStatus;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.offbytwo.jenkins.model.QueueItem;
import com.offbytwo.jenkins.model.QueueReference;
import net.sf.json.JSONObject;

public class BuildThread implements Runnable {
	
	private String buildName;
	private Long buildId;
	private static final Long DEFAULT_RETRY_INTERVAL = 200L;
	private static QueueReference queueRef;
	private static QueueItem queueItem;	 
	private static Session session;
	JenkinsServer jenkins; 
	private JobStatusRepo jobsRepository;
	//HashMap<String, String> JobParams = new HashMap<String, String>();
	Map<String, String> JobParams = new HashMap<String, String>();
	public BuildThread()
	{
		
	}
	@Autowired
	//public BuildThread(long buildId,String buildName, JobStatusRepo jobsRepository,HashMap<String, String> JobParams) {
	public BuildThread(long buildId,String buildName, JobStatusRepo jobsRepository,Map<String, String> JobParams) 
	{
		this.buildId = buildId;
		this.buildName = buildName;
		this.jobsRepository = jobsRepository;
		this.JobParams =JobParams;
	} 

	@Override
	public void run() {
		try {		
			//jenkins
			 jenkins = new JenkinsServer(new URI(this.Url), this.Username, this.password);
			JobWithDetails jobinfo = jenkins.getJob(this.buildName);
			if(JobParams.size()>0)
			{
				System.out.println("params :"+JobParams.keySet());
				System.out.println("param values :"+JobParams.values());
				System.out.println("params sent :"+JobParams);
				queueRef=jobinfo.build(this.JobParams, true);				
			}
			else
			{
			queueRef=jobinfo.build(true);
			}
			queueItem = jenkins.getQueueItem(queueRef);
			while (queueItem.getExecutable() == null) {		
			       Thread.sleep(DEFAULT_RETRY_INTERVAL);
			       queueItem = jenkins.getQueueItem(queueRef);			      
			}					
			Build build = jenkins.getBuild(queueItem);				
			while(build.details().isBuilding() == true)
			{						 
				continue;
			}

			//by now build has completed i.e succeded or failed

			// build success
			if(build.details().getResult() == build.details().getResult().SUCCESS) {
				Optional<JobStatus> currentBuildRecord = this.jobsRepository.findById(buildId);
				currentBuildRecord.ifPresent(currentBuild -> {
					currentBuild.setBuildstatus("Successfully Completed");
					jobsRepository.saveAndFlush(currentBuild);
				});
			}

			//build fail
			if (build.details().getResult() == build.details().getResult().FAILURE) {
				Optional<JobStatus> currentBuildRecord = this.jobsRepository.findById(buildId);
				currentBuildRecord.ifPresent(currentBuild -> {
					currentBuild.setBuildstatus("Build Failed");
					jobsRepository.saveAndFlush(currentBuild);
				});
			}
			
			if (build.details().getResult() == build.details().getResult().ABORTED)
			{
				Optional<JobStatus> currentBuildRecord = this.jobsRepository.findById(buildId);
				currentBuildRecord.ifPresent(currentBuild -> {
					currentBuild.setBuildstatus("Build Stopped");
					jobsRepository.saveAndFlush(currentBuild);
				});
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void stopThread() {
	       //running = false;
	       //interrupt();
	       try {	       
		 jenkins = new JenkinsServer(new URI(this.Url), this.Username, this.password); 
		while(queueItem == null)
		{
	           Thread.sleep(50L);
		}
		Build build = jenkins.getBuild(queueItem);
	
		JSONObject jsonobj = new JSONObject();
		if(build.details().isBuilding()==true)
		{
		  build.Stop(true);		  	          
		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	   }
	}
	

