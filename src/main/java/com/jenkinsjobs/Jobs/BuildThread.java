package com.jenkinsjobs.Jobs;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.management.Query;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.NativeQuery;
//import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Build;
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
	
	private JobStatusRepo jobsRepository;
	public BuildThread()
	{
		
	}
	@Autowired
	public BuildThread(long buildId,String buildName, JobStatusRepo jobsRepository) {
		this.buildId = buildId;
		this.buildName = buildName;
		this.jobsRepository = jobsRepository;
	} 

	@Override
	public void run() {
		try {
			//jenkins = new JenkinsServer(new URI("https://kone.iagilepro.com"), "agile.pro@kone.com", "infy1234");
			 jenkins = new JenkinsServer(new URI("http://localhost:8080"), "kit", "kit");
			JobWithDetails jobinfo = jenkins.getJob(this.buildName);
			if(JobParams.size()>0)
			{
				queueRef=jobinfo.build(JobParams, true);
			}
			else
			{
			queueRef=jobinfo.build(true);
			}
			queueItem = jenkins.getQueueItem(queueRef);
			Build build = jenkins.getBuild(queueItem);		
			BuildWithDetails builddetails = build.details();
			if(builddetails.getParameters() != null)
			{
				System.out.println("parameterized");
			}
		    JSONObject jsonobj = new JSONObject();				
			while (queueItem.getExecutable() == null) {		
			       Thread.sleep(DEFAULT_RETRY_INTERVAL);
			       queueItem = jenkins.getQueueItem(queueRef);
			      
			}
			build = jenkins.getBuild(queueItem);				
			while(build.details().isBuilding() == true)
			{						 
				continue;
			}

			//by now build has completed i.e succeded or failed

			// build success
			if(build.details().getResult() == build.details().getResult().SUCCESS) {
				Optional<JobStatus> currentBuildRecord = this.jobsRepository.findById(buildId);
				currentBuildRecord.ifPresent(currentBuild -> {
					currentBuild.setBuildstatus("SUCCESS");
					jobsRepository.saveAndFlush(currentBuild);
				});
			}

			//build fail
			if (build.details().getResult() == build.details().getResult().FAILURE) {
				Optional<JobStatus> currentBuildRecord = this.jobsRepository.findById(buildId);
				currentBuildRecord.ifPresent(currentBuild -> {
					currentBuild.setBuildstatus("FAILURE");
					jobsRepository.saveAndFlush(currentBuild);
				});
			}
			
			if (build.details().getResult() == build.details().getResult().ABORTED)
			{
				Optional<JobStatus> currentBuildRecord = this.jobsRepository.findById(buildId);
				currentBuildRecord.ifPresent(currentBuild -> {
					currentBuild.setBuildstatus("ABORTED");
					jobsRepository.saveAndFlush(currentBuild);
				});
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void stopThread(long buildId) {
	       //running = false;
	       //interrupt();
	       try {
	    	   System.out.println("buidld id in stop :"+buildId);
	    	   Optional<JobStatus> currentBuildRecord = this.jobsRepository.findById(buildId);
				currentBuildRecord.ifPresent(currentBuild -> {
					currentBuild.setBuildstatus("discontinuing..");
					jobsRepository.saveAndFlush(currentBuild);
				});
		
		//jenkins = new JenkinsServer(new URI("https://kone.iagilepro.com"), "agile.pro@kone.com", "infy1234");
				 jenkins = new JenkinsServer(new URI("http://localhost:8080"), "kit", "kit");
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
	

