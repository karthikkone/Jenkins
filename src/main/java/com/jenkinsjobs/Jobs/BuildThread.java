package com.jenkinsjobs.Jobs;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
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
	private AuthDataRepository  authDataRepository;
	public BuildThread()
	{
		
	}
	public BuildThread(long buildId,String buildName) {
		this.buildId = buildId;
		this.buildName = buildName;
	}
	@Autowired
    public BuildThread(AuthDataRepository  authDataRepository) {
        this.authDataRepository = authDataRepository;     
    }
	/*public void ConnectDB()
	{
		SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
		
	}*/
	private static void CheckBuildStatus(SessionFactory sessionFactory, long buildId) {
		JSONObject jsonobj = new JSONObject();	
        System.out.println("-- loading Jobs --");
        Session session = sessionFactory.openSession();
        @SuppressWarnings("unchecked")
        List<JobStatus> jobs = session.createQuery("FROM JobStatus WHERE buildid= :buildId")
        .setParameter("buildId", buildId)
        .list();
        
        //jobs.forEach((x) -> System.out.printf("- %s%n", x));          
        for(int i=0;i<jobs.size();i++)
        {
        	System.err.println("jobs in array :"+jobs.get(i).getBuildname()+" "+jobs.get(i).getBuildstatus());
        }
       // session.close();
    }
    private static void CreateBuildInDB(SessionFactory sessionFactory,long buildId,String BuildName,String BuildStatus) {
    	Session session = sessionFactory.openSession();
    	try {
    	JobStatus s1 = new JobStatus(buildId, BuildName,BuildStatus);        
        System.out.println("-- persisting jobs --");    
        session.beginTransaction();
        session.save(s1);       
        session.getTransaction().commit();
        //load(sessionFactory);
        CheckBuildStatus(sessionFactory, buildId);
    	}
    	catch(Exception e)
    	{
    		System.out.println(e.getMessage());
    	}
    	/*finally
    	{
    		session.close();
    	}*/
    }
    private void UpdateBuildInDB(long BuildId,String BuildStatus)
    { 	
    	
    	System.out.println("updating build  :"+BuildId+" status "+BuildStatus);
        /*Query updated = session.createQuery("update JobStatus SET BuildStatus =:buildStatus WHERE buildid =:buildId")
        		.setParameter("buildStatus", BuilsStatus)
        		.setParameter("buildId", BuildId);*/
    	//Query updated = session.createQuery("update JobStatus SET buildstatus ="+"'"+BuildStatus+"'"+" WHERE buildid ="+BuildId);
    	
    	if(authDataRepository.findById(buildId).isPresent());
    	{
    		JobStatus updatedjobststaus=new JobStatus();
    		updatedjobststaus.setBuildstatus(BuildStatus);
		    authDataRepository.save(updatedjobststaus);
    	}
        System.out.println("value of updated var :"+authDataRepository.findAll());
        //update
    	/*int no=updated.executeUpdate();
    	System.out.println("no of records updated :"+no);
    	session.getTransaction().commit();
    	
    	@SuppressWarnings("unchecked")
    	List<JobStatus> jobs = session.createQuery("FROM JobStatus WHERE buildid=:buildId")
    			.setParameter("buildId", BuildId).list();
    	
    	for(int i=0;i<jobs.size();i++)
        {
    	System.out.println("final result :"+jobs.get(i).getBuildname()+" "+jobs.get(i).getBuildstatus());
        }
    	session.close();*/
    }
	@Override
	public void run() {
		// TODO Auto-generated method stub
		JenkinsServer jenkins;
		SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
		try {
			jenkins = new JenkinsServer(new URI("http://localhost:8080/"), "kit", "kit");			 
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
				UpdateBuildInDB(this.buildId,"Success");
			}
			else if (build.details().getResult() == build.details().getResult().FAILURE) {
				UpdateBuildInDB(this.buildId,"Failure");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
			
		
		
	}
	
}
