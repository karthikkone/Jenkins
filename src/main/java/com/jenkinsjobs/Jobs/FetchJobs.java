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
    private AuthDataRepository  authDataRepository;
	public FetchJobs()
	{
		
	}
	@Autowired
    public FetchJobs(AuthDataRepository  authDataRepository) {
        this.authDataRepository = authDataRepository;     
    }
	@RequestMapping(value="/jobs", method=RequestMethod.GET)
	public JSONObject getJobs() throws Exception 
	{
		 try {
	         //jenkins = new JenkinsServer(new URI("https://kone.iagilepro.com"), "agilepro", "infosys@123");
			 jenkins = new JenkinsServer(new URI("http://localhost:8080/"), "kit", "kit");
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
		 JSONObject jsonobj = new JSONObject();	       
		JobStatus jobStat = new JobStatus();
		jobStat.setBuildname(buildname);
		jobStat.setBuildstatus("In Progress");
		System.out.println("buildname :"+jobStat.getBuildname());
		authDataRepository.saveAndFlush(jobStat);
		Thread bThread = new Thread(new BuildThread(jobStat.getBuildid(),buildname));
	    bThread.start();
	    jsonobj.put("result", "success");
		return jsonobj;
	}
	@RequestMapping(value="/CheckStatus",params={"queueid"},method=RequestMethod.GET)	
	public JSONObject CheckStatus(@RequestParam("queueid") QueueItem queueid) throws Exception 
	//public boolean CheckStatus(String q)
	{
	    Build build;
	    JSONObject jsonobj = new JSONObject();	
		try {
			jenkins = new JenkinsServer(new URI("http://localhost:8080/"), "kit", "kit");			
			build = jenkins.getBuild(queueid);
			while(build.details().isBuilding() == true)
			{						 
				//Thread.sleep(DEFAULT_RETRY_INTERVAL);
				 //System.out.println(Building());
				//return build.details().isBuilding();
				jsonobj.put("Status:","In Progress");	
				return jsonobj;
			}
			System.out.println("Queue item in check ,,..."+build.details().getResult());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}							
		 catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e) {
	         System.err.println(e.getMessage());
	         
	     	}		
		jsonobj.put("Status:", "null");	
		return jsonobj;
	}
	@RequestMapping(value="/CheckResult",params={"queueid"},method=RequestMethod.GET)	
	public JSONObject CheckResult(@RequestParam("queueid") QueueItem queueid) throws Exception 
	//public String CheckResult(QueueItem q)
	{

	    Build build;
	    JSONObject jsonobj = new JSONObject();	
		try {
			jenkins = new JenkinsServer(new URI("http://localhost:8080/"), "kit", "kit");
			build = jenkins.getBuild(queueid);
			if(build.details().getResult() == build.details().getResult().SUCCESS)
			{						 
				//Thread.sleep(DEFAULT_RETRY_INTERVAL);
				 //System.out.println("Last build :"+build.details().get);
				//return "Successfully completed";
				jsonobj.put("Result:", "Successfully completed");	
				return jsonobj;
			}
			else if (build.details().getResult() == build.details().getResult().FAILURE) {
				jsonobj.put("Result:", "Failed");	
				return jsonobj;
			}
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}							
		 catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e) {
	         System.err.println(e.getMessage());
	         
	     	}	
		
		jsonobj.put("Result:", "error");	
		return jsonobj;
	}
	@RequestMapping(value="/Stopjobs",method=RequestMethod.GET)
	public JSONObject StopJob() throws Exception 
	{
		try{
		//jenkins = new JenkinsServer(new URI("https://kone.iagilepro.com"), "agilepro", "infosys@123");
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