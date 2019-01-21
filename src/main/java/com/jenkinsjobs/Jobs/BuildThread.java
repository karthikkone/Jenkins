package com.jenkinsjobs.Jobs;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
//import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.offbytwo.jenkins.model.QueueItem;
import com.offbytwo.jenkins.model.QueueReference;

import net.sf.json.JSONObject;

public class BuildThread{
	
	private String buildName;
	private Long buildId;
	private static final Long DEFAULT_RETRY_INTERVAL = 200L;
	private static QueueReference queueRef;
	private static QueueItem queueItem;	 
	public BuildThread()
	{
		
	}
	public BuildThread(long buildId,String buildName) {
		this.buildId = buildId;
		this.buildName = buildName;
	} 
	
	public String Start(SessionFactory s) {
		// TODO Auto-generated method stub
		JenkinsServer jenkins;
		SessionFactory sessionFactory = s;
		try {
			//jenkins = new JenkinsServer(new URI("http://localhost:8080/"), "kit", "kit");	
			jenkins = new JenkinsServer(new URI("https://kone.iagilepro.com"), "agilepro", "infosys@123");
			 JobWithDetails jobinfo = jenkins.getJob(this.buildName);
			queueRef=jobinfo.build(true);	
			//CreateBuildInDB(sessionFactory,1,this.buildName,"In Progress");
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
			if(build.details().getResult() == build.details().getResult().SUCCESS)
			{						 
				return "Successfully Completed";
			}
			else if (build.details().getResult() == build.details().getResult().FAILURE) {
				return "build Failed";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
			
		return null;
		
	}
	
}
