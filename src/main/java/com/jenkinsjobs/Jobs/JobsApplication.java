package com.jenkinsjobs.Jobs;

//import org.json.JSONObject;
import net.sf.json.*;

import java.net.URISyntaxException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildWithDetails;

@SpringBootApplication
@EnableJpaRepositories("com.jenkinsjobs.Jobs")
public class JobsApplication {
	 private static JobStatus authDataRepository;
	 
	public static void main(String[] args) throws URISyntaxException {
		SpringApplication.run(JobsApplication.class, args);
		
		//FetchJobs obj = new FetchJobs();		
		try {
			//JSONObject jarray = obj.getJobs();
			/*for(int i=0;i<jarray.size();i++)
			{
				System.out.println(jarray.toString(i));
			}*/
			//obj.StartJob("testjob1");
			//obj.StartJob("Salesforce_retrieve");
		
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
