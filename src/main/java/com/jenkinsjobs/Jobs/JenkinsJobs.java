package com.jenkinsjobs.Jobs;

import java.io.IOException;
import java.io.StringReader;
//import java.awt.List;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import net.sf.json.*;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.jenkinsjobs.model.JobConfiguration;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.offbytwo.jenkins.model.QueueItem;
import com.offbytwo.jenkins.model.QueueReference;
import com.offbytwo.jenkins.model.Job;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.offbytwo.jenkins.model.QueueItem;
import com.offbytwo.jenkins.model.QueueReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
@RestController
public class JenkinsJobs {
	
	/*@Value("${jobs.url}")
    private String Url;

    @Value("${jobs.username}")
    private String Username;

    @Value("${jobs.password}")
    private String password;*/
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
     private SpringTemplateEngine templateEngine;
     
	@Autowired
	public JenkinsJobs(JobStatusRepo repository, SpringTemplateEngine templateEngine) {
		this.jobsRepository = repository;
		this.templateEngine = templateEngine;
	}
	
    /*@Autowired
    private JobStatusRepo jobsrepository;*/
	@RequestMapping(value="/jobs", method=RequestMethod.GET)
	public JSONObject getJobs() throws Exception 
	{
		 try {	         
		 //jenkins = 
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
		//List<String> Paramtypes = new ArrayList<String>();
		HashMap<String, String> Paramtypes = new HashMap<String, String>();
		HashMap<String, String>  Params = new HashMap<String, String>();
		//JSONObject config = ConfigParser.parseConfigFile("C:\\Users\\kirti.annajigar\\Workspace\\Jenkins-JPA-master\\src\\main\\resources\\config.xml");
		//jenkins =
	        //jenkins = 
		JobWithDetails jobinfo = jenkins.getJob(buildname);
		String jobxml = jenkins.getJobXml(buildname);		
		System.out.println("XML :"+jobxml);	
		org.w3c.dom.Document doc = convertStringToXMLDocument(jobxml);	
		NodeList list = doc.getElementsByTagName("parameterDefinitions");
	    	for (int i=0; i< list.getLength(); i++) {	    	
	        Node Param = list.item(i);
            	System.out.println("list size :"+list.getLength());
	        if(Param.hasChildNodes()){	        	
	        	
	            for(int j=0; j< Param.getChildNodes().getLength(); j++)
	        	{
	            	Node ParamType = Param.getChildNodes().item(j).getNextSibling();
			   //if (ParamType != null && ParamType.getNodeType() == ParamType.ELEMENT_NODE)
	            	//{
	            	//Paramtypes.put("Paramtype",ParamType.getNodeName());
			//Paramtypes.put(ParamType.getNodeName(),"");
	            	//System.out.println("to check text :"+ParamType.getNodeName());
	            	//}
	            	//System.out.println("param types in loop :"+ParamType.getChildNodes().item(0).getNodeName());
	            //NodeList ParamTypes = doc.getElementsByTagName(ParamType.getNodeName());
	            //System.out.println("ParamTypes  ka length :"+ParamTypes.getLength());
	             //for(int k=0;k<ParamTypes.getLength();k++)
	             //{
	            	// Node ParamType1 = ParamTypes.item(k);
	            	//Node ParamType1 = ParamTypes.item(0);
	            	if(ParamType != null)
	            	{
			//Paramtypes.add(ParamType.getNodeName());
			 //Paramtypes.put("Paramtype",ParamType.getNodeName());
	            	if(ParamType.hasChildNodes())
	            	{
	            	 Node ParamName1 = ParamType.getChildNodes().item(0).getNextSibling();	            	 
	            	 System.out.println("ParamName in paramtypes:"+ParamName1.getNodeName());
	            	 System.out.println("ParamValues in paramtypes:"+ParamName1.getChildNodes().item(0).getNodeValue());
	            	 Params.put(ParamName1.getChildNodes().item(0).getNodeValue(), ParamType.getNodeName());
	            	}
			
	            	}
	            	else
	            	{
	            		break;
	            	}
	             //}
	           
	        	}
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
		Jsonobj.put("Paramtype",Paramtypes);	
		Jsonobj.put("BuildParams",Params);
		if(Params.size() == 0)
		{
		Thread b= new Thread(new BuildThread(selectedJob.getBuildid(),buildname,jobsRepository,Params));
		b.start();
		}
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
		//jenkins = 
		//jenkins = 
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
	@RequestMapping(value="/StartjobsWithParams",params={"buildid","buildname"},method=RequestMethod.POST)	
	//public JSONObject StartJobWithParams(@RequestParam("buildid") long buildid,@RequestParam("buildname") String buildname,@RequestParam("Params") HashMap<String, String> Params) throws Exception 
	public void StartjobsWithParams(long buildid,String buildname,@RequestBody Map<String, String> Params) throws Exception
	{
	//public void StartJob(String buildname) throws Exception
	
		try {
			System.out.println("Parametrs received from URL :"+Params);
			Thread build= new Thread(new BuildThread(buildid,buildname,jobsRepository,Params));
			build.start();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		//return null;
		
	}		
	@RequestMapping(value="/Stopjobs",method=RequestMethod.GET)
	public void StopJob() throws Exception 
	{
	        try{				
			
			BuildThread b = new BuildThread();
		        b.stopThread();
		}
		 catch (Exception e) {
	         System.err.println(e.getMessage());
	         throw e;
	     }
		
	
	
	}
	
	//create new job
	@RequestMapping(value="/createjob", method=RequestMethod.POST)
	public ResponseEntity createJob(@RequestBody JobConfiguration jobDetails) {
		String xml = "hello";
		HashMap<String,String> jobConfig = new HashMap<String,String>();
		Context context = new Context();
		context.setVariable("jobConfig", jobConfig);
		
		jobConfig.put("description", jobDetails.getDescription());
		jobConfig.put("github_project_url", jobDetails.getGithubProject());
		jobConfig.put("github_credential_id", jobDetails.getGithubCredentialId());
		jobConfig.put("git_branch", jobDetails.getGitBranch());
		jobConfig.put("batch_script", jobDetails.getBatchScript());
		jobConfig.put("targets", jobDetails.getBuildTargets());
		String xmlConfig = this.templateEngine.process("job-config", context);
		
		
		try {
			//jenkins = 
			jenkins.createJob(jobDetails.getJobName(), xmlConfig, true);
			
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity(HttpStatus.OK);
	}
	
	public org.w3c.dom.Document convertStringToXMLDocument(String xmlString)
    {
        //Parser that produces DOM object trees from XML content
		
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         
        //API to obtain DOM Document instance
        DocumentBuilder builder = null;
        try
        {
            //Create DocumentBuilder with default configuration
            builder = factory.newDocumentBuilder();             
            //Parse the content to Document object
            //String jobxml = jenkins.getJobXml(buildname);
            org.w3c.dom.Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
            return doc;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
