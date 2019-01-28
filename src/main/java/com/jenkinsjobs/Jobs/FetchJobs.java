package com.jenkinsjobs.Jobs;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
//import java.awt.List;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import javax.print.attribute.standard.JobState;


import java.util.List;
import java.util.ArrayList;
import java.util.Date;

import net.sf.json.*;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.client.JenkinsHttpConnection;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildResult;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.offbytwo.jenkins.model.Job;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.offbytwo.jenkins.model.Queue;
import com.offbytwo.jenkins.model.QueueItem;
import com.offbytwo.jenkins.model.QueueReference;
@RestController
public class FetchJobs {
	
	@Value("${jobs.url}")
    private String Url;

    @Value("${jobs.username}")
    private String Username;

    @Value("${jobs.password}")
    private String password;
    public JenkinsServer jenkins;
    //private final Long retryInterval;
    private static final Long DEFAULT_RETRY_INTERVAL = 200L;
    boolean flag=false;
    private static QueueReference queueRef;
    private static QueueItem queueItem;
    //private AuthDataRepository  authDataRepository;
    private static SessionFactory sessionFactory;
    private static Session session;
	public FetchJobs()
	{
		sessionFactory = new Configuration().configure().buildSessionFactory();
	}
	
	@RequestMapping(value="/jobs", method=RequestMethod.GET)
	public JSONObject getJobs() throws Exception 
	{
		 try {
	         
	         //jenkins = new JenkinsServer(new URI("http://localhost:8080/"), "kit", "kit");
	         List<String> jobnames = new ArrayList<String>();    
	         Map<String, Job> jobs = jenkins.getJobs();
	         //System.out.println("new jobs... :"+jobs);
	         JSONObject jsonobj = new JSONObject();	         
	         for (String jobnm: jobs.keySet())
	         {
	             jobnames.add(jobnm);
	             
	         }
	         jsonobj.put("JobNames", jobnames);
	         return jsonobj;
	     } 
		 catch (Exception e) {
	         System.err.println(e.getMessage());
	         throw e;
	     }
		finally 
		{
		jenkins.close();
		}
	}
	
	@RequestMapping(value="/Startjobs",params={"buildname"},method=RequestMethod.GET)	
	public JSONObject StartJob(@RequestParam("buildname") String buildname) throws Exception 
	//public void StartJob(String buildname) throws Exception
	{
		String status;		
		JSONObject jsonobj = new JSONObject();	       
		JobStatus jobStat = new JobStatus();
		jobStat.setBuildname(buildname);
		jobStat.setBuildstatus("In Progress");
		System.out.println("buildname :"+jobStat.getBuildname());		
		session = sessionFactory.openSession();
		session.beginTransaction();
		session.save(jobStat);       
		session.getTransaction().commit();
		List<JobStatus> jobs1 = session.createQuery("FROM JobStatus").list();
        //jobs.forEach((x) -> System.out.printf("- %s%n", x));        
		jsonobj.put("Build id", jobStat.getBuildid());
		jsonobj.put("Build Name", jobStat.getBuildname());
		jsonobj.put("Build Status", jobStat.getBuildstatus());
        for(int i=0;i<jobs1.size();i++)
        {
        	System.err.println("jobs in array :"+jobs1.get(i).getBuildid()+" "+jobs1.get(i).getBuildname()+" "+jobs1.get(i).getBuildstatus());
        }
		BuildThread b= new BuildThread(jobStat.getBuildid(),buildname);
		b.Start(sessionFactory);
		//status=b.Start(sessionFactory);
		//jobStat.setBuildstatus(status);
		List<JobStatus> jobs = session.createQuery("FROM JobStatus").list();
        //jobs.forEach((x) -> System.out.printf("- %s%n", x));          
        for(int i=0;i<jobs.size();i++)
        {
        	System.err.println("jobs in array :"+jobs.get(i).getBuildid()+" "+jobs.get(i).getBuildname()+" "+jobs.get(i).getBuildstatus());
        }	    
		return jsonobj;
	}
			
	@RequestMapping(value="/Stopjobs",method=RequestMethod.GET)
	public JSONObject StopJob() throws Exception 
	{
		try{
		
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
	       		
		return jsonobj; 
		}
		 catch (Exception e) {
	         System.err.println(e.getMessage());
	         throw e;
	     }
		finally 
		{
		jenkins.close();
		}
	
	}
}
