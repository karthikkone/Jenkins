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

public class BuildThread{
	
	private String buildName;
	private Long buildId;
	private static final Long DEFAULT_RETRY_INTERVAL = 200L;
	private static QueueReference queueRef;
	private static QueueItem queueItem;	 
	private static Session session;
	public BuildThread()
	{
		
	}
	public BuildThread(long buildId,String buildName) {
		this.buildId = buildId;
		this.buildName = buildName;
	} 
	
	public void Start(SessionFactory s) {
		// TODO Auto-generated method stub
		JenkinsServer jenkins;
		SessionFactory sessionFactory = s;
		try {
			//jenkins = new JenkinsServer(new URI("http://localhost:8080/"), "kit", "kit");		
			 jenkins = new JenkinsServer(new URI("https://kone.iagilepro.com"), "agile.pro@kone.com", "Infy1234");
			 JobWithDetails jobinfo = jenkins.getJob(this.buildName);
			queueRef=jobinfo.build(true);	
			session = sessionFactory.openSession();
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
				System.out.println("inside success");
				String Updatequery= "UPDATE JobStatus set buildstatus = :buildstatus "+"WHERE buildid = :buildid";
				System.out.println("value in updatequry :"+Updatequery);
				org.hibernate.query.Query Update= session.createQuery(Updatequery);
				Update.setParameter("buildstatus", "Successfully Completed");
				Update.setParameter("buildid", this.buildId);
				session.beginTransaction();
				int result = Update.executeUpdate();
				session.getTransaction().commit();
				System.out.println("Rows affected: " + result);
				List<JobStatus> jobs1 = session.createQuery("FROM JobStatus where buildid="+this.buildId).list();
			    System.out.println("result :"+jobs1.get(0).getBuildstatus());
				//Jsonobj.put("Result", jobs1.get(i).getBuildstatus());
		       
				//return "Successfully Completed";
			}
			else if (build.details().getResult() == build.details().getResult().FAILURE) {
				System.out.println("inside Failed");
				String Updatequery= "UPDATE JobStatus set buildstatus = :buildstatus "+"WHERE buildid = :buildid";
				System.out.println("value in updatequry :"+Updatequery);
				org.hibernate.query.Query Update= session.createQuery(Updatequery);
				Update.setParameter("buildstatus", "Failed");
				Update.setParameter("buildid", this.buildId);
				session.beginTransaction();
				int result = Update.executeUpdate();
				session.getTransaction().commit();
				System.out.println("Rows affected: " + result);
				List<JobStatus> jobs1 = session.createQuery("FROM JobStatus where buildid="+this.buildId).list();
			    System.out.println("result :"+jobs1.get(0).getBuildstatus());
				//return "build Failed";
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
	
}
	

