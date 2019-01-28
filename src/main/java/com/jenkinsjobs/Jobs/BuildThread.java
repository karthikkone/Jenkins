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

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.offbytwo.jenkins.model.QueueItem;
import com.offbytwo.jenkins.model.QueueReference;

import net.sf.json.JSONObject;

public class BuildThread implements Runnable{
	
	private String buildName;
	private Long buildId;
	private static final Long DEFAULT_RETRY_INTERVAL = 200L;
	private static QueueReference queueRef;
	private static QueueItem queueItem;	 
	 @Autowired
	    private BuildService service;
	 @Autowired
	 private JobStatusRepo jobsrepo;
	public BuildThread()
	{
		
	}
	public BuildThread(long buildId,String buildName) {
		this.buildId = buildId;
		this.buildName = buildName;
	} 
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		JenkinsServer jenkins;
		try {
			jenkins = new JenkinsServer(new URI("https://kone.iagilepro.com"), "agile.pro@kone.com", "infy1234");			 
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
			JobStatus job = service.getbuild(this.buildId);
			Optional<JobStatus> jobstatus = jobsrepo.findById(buildId);
			

				
			
			
			if(build.details().getResult() == build.details().getResult().SUCCESS)
			{					
				//job.setBuildstatus("SUCCESS");
				//service.updateBuild(job);
				
				jobstatus.ifPresent(currentBuild -> {
					currentBuild.setBuildstatus("SUCCESS");
					jobsrepo.saveAndFlush(currentBuild);
				});
				
			}
			else if (build.details().getResult() == build.details().getResult().FAILURE) {
				
				//job.setBuildstatus("FAILURE");
				//service.updateBuild(job);
				
				jobstatus.ifPresent(currentBuild -> {
					currentBuild.setBuildstatus("FAILURE");
					jobsrepo.saveAndFlush(currentBuild);
				});
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
			
		//return null;
		
	}
	/*public List<JobStatus> CheckStatus(SessionFactory s,long buildid)
	{
	    
		try {
			JSONObject Jsonobj=new JSONObject();
			SessionFactory sessionFactory = s;
			session = sessionFactory.openSession();
			List<JobStatus> jobs1 = session.createQuery("FROM JobStatus where buildid="+buildid).list();
			for(int i=0;i<jobs1.size();i++)
	        {
				System.out.println("result :"+jobs1.get(i).getBuildstatus());
			//Jsonobj.put("Result", jobs1.get(i).getBuildstatus());
	        }
			//return Jsonobj;
			return jobs1;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}							
		return null;
	}	*/
	
		// TODO Auto-generated method stub
		
	}
	

