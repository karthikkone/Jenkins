package com.jenkinsjobs.Jobs;

import java.io.StringReader;
//import java.awt.List;
import java.net.URI;
import java.util.Map;
import java.util.Optional;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import net.sf.json.*;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Job;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.offbytwo.jenkins.model.QueueItem;
import com.offbytwo.jenkins.model.QueueReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@RestController
public class JenkinsJobs {
	
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
    //private JobStatusRepo Repo;
    private static SessionFactory sessionFactory;
    private static Session session;
    /*@Autowired
    private JobStatusRepo jobsrepository;*/
     private JobStatusRepo jobsRepository;
     private final Logger logger = LoggerFactory.getLogger(JenkinsJobs.class);

	@Autowired
	public JenkinsJobs(JobStatusRepo repository) {
		this.jobsRepository = repository;
	}
	
    /*@Autowired
    private JobStatusRepo jobsrepository;*/
	@RequestMapping(value="/jobs", method=RequestMethod.GET)
	public JSONObject getJobs() throws Exception 
	{
		 try {	         
		 jenkins = new JenkinsServer(new URI("https://kone.iagilepro.com"), "agile.pro@kone.com", "infy1234");
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
		try {
		JSONObject Jsonobj = new JSONObject();	 
		//List<String> Params = new ArrayList<String>();
		HashMap<String, String>  Params = new HashMap<String, String>();
		//JSONObject config = ConfigParser.parseConfigFile("C:\\Users\\kirti.annajigar\\Workspace\\Jenkins-JPA-master\\src\\main\\resources\\config.xml");
		jenkins = new JenkinsServer(new URI("http://localhost:8080"), "kit", "kit");		
		JobWithDetails jobinfo = jenkins.getJob(buildname);
		String jobxml = jenkins.getJobXml(buildname);		
		System.out.println("XML :"+jobxml);	
		org.w3c.dom.Document doc = convertStringToXMLDocument(jobxml);	
		NodeList list = doc.getDocumentElement().getElementsByTagName("name");		
		NodeList subList =null;
		String subListattribute =null;
		String subListvalue =null;
		System.out.println("list.getLength() :"+list.getLength());
		 if (list != null && list.getLength() > 0) {
			 for(int i=0;i<list.getLength();i++)
			 {
			 	subList = list.item(i).getChildNodes();					 	
	            subListvalue = subList.item(0).getNodeValue();
	            System.out.println("sublist value :"+subListvalue);  	
	            Params.put(subListvalue, "");
			 }
		 }	 
		  
		System.out.println("After converting string to xml :"+doc.getFirstChild().getNodeName());	
		JobStatus jobStat = new JobStatus();
		jobStat.setBuildname(buildname);
		jobStat.setBuildstatus("In Progress");	
		JobStatus selectedJob = jobsRepository.saveAndFlush(jobStat);   
		Jsonobj.put("Buildid", selectedJob.getBuildid());
		Jsonobj.put("Buildname", selectedJob.getBuildname());
		Jsonobj.put("Buildstatus", selectedJob.getBuildstatus());
		Jsonobj.put("BuildParams",Params);
		Thread b= new Thread(new BuildThread(selectedJob.getBuildid(),buildname,jobsRepository,Params));
		b.start();
		//BuildThread b = new BuildThread(selectedJob.getBuildid(),buildname,jobsRepository);
		//b.startJob();
		return Jsonobj;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	@RequestMapping(value="/CheckStatus",params={"buildid"},method=RequestMethod.GET)	
	public JSONObject CheckStatus(@RequestParam("buildid") long buildid) throws Exception 
	//public JSONObject CheckStatus(long buildid)
	{
		try
		{
		JSONObject Jsonobj = new JSONObject();
		//SessionFactory sessionFactory = s;
		
			//JobStatus job = service.getbuild(buildid);	
			//JobStatus job = jobsrepository.getOne(buildid);
			JobStatus job = jobsRepository.getOne(buildid);
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
	}	
	@RequestMapping(value="/StartjobsWithParams",params={"buildid","buildname","Params"},method=RequestMethod.GET)	
	public JSONObject StartJobWithParams(@RequestParam("buildid") long buildid,@RequestParam("buildname") String buildname,@RequestParam("Params") HashMap<String, String> Params) throws Exception 
	//public void StartJob(String buildname) throws Exception
	{
		try {
			Thread build= new Thread(new BuildThread(buildid,buildname,jobsRepository,Params));
			build.start();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
		
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
