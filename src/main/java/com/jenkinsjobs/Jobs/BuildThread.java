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
			
			JobWithDetails jobinfo = jenkins.getJob(this.buildName);
			queueRef=jobinfo.build(true);
			queueItem = jenkins.getQueueItem(queueRef);
		    JSONObject jsonobj = new JSONObject();				
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
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/*public void StartWihSessionFactory(SessionFactory s) {
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		JenkinsServer jenkins;
		try {
			 	 
			JobWithDetails jobinfo = jenkins.getJob(this.buildName);
			queueRef=jobinfo.build(true);			
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
			//JobStatus job = service.getbuild(this.buildId);
			JobStatus job = jobsrepo.getOne(this.buildId);
			System.out.println("job :"+job.getBuildname());
			//Optional<JobStatus> jobstatus = jobsrepo.findById(buildId);			
			if(build.details().getResult() == build.details().getResult().SUCCESS)
			{					
				job.setBuildstatus("SUCCESS");
				service.updateBuild(job);
				
				/*jobstatus.ifPresent(currentBuild -> {
					currentBuild.setBuildstatus("SUCCESS");
					jobsrepo.saveAndFlush(currentBuild);
				});*/
				
		//	}
		//	else if (build.details().getResult() == build.details().getResult().FAILURE) {
		//		
		//		job.setBuildstatus("FAILURE");
		//		service.updateBuild(job);
				
				/*jobstatus.ifPresent(currentBuild -> {
					currentBuild.setBuildstatus("FAILURE");
					jobsrepo.saveAndFlush(currentBuild);
				});*/
				
		//	}
		//} catch (Exception e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}		
			
		//return null;
		
	//}
	/*public List<JobStatus> CheckStatus(SessionFactory s,long buildid)
	}
	/*@RequestMapping(value="/CheckStatus",params={"buildid"},method=RequestMethod.GET)	
	public JSONObject CheckStatus(@RequestParam("buildid") long buildid) throws Exception 
	//public JSONObject CheckStatus(long buildid)
	{
		try
		{
		JSONObject Jsonobj = new JSONObject();
		//SessionFactory sessionFactory = s;
		
			JobStatus job = service.getbuild(buildid);		
			Jsonobj.put("Buildid", job.getBuildid());
			Jsonobj.put("Buildname", job.getBuildname());
			Jsonobj.put("Buildstatus", job.getBuildstatus());
			return Jsonobj;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}*/	
	
		// TODO Auto-generated method stub
		
	}
	

