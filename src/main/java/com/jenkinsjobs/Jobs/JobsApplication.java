package com.jenkinsjobs.Jobs;

//import org.json.JSONObject;
import net.sf.json.*;

import java.net.URISyntaxException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildWithDetails;

@SpringBootApplication
public class JobsApplication {

	public static void main(String[] args) throws URISyntaxException {
		SpringApplication.run(JobsApplication.class, args);
		FetchJobs obj = new FetchJobs();		
		try {
			JSONObject jarray = obj.getJobs();
			for(int i=0;i<jarray.size();i++)
			{
				System.out.println(jarray.toString(i));
			}
			//obj.StartJob("testjob2");
			obj.StartJob("Salesforce_retrieve");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
