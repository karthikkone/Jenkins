package com.jenkinsjobs.Jobs;

import net.sf.json.*;

import java.net.URISyntaxException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildWithDetails;

import antlr.collections.List;

@SpringBootApplication
@EnableJpaRepositories("com.jenkinsjobs.Jobs")
public class JobsApplication {
	 //private static JobStatus authDataRepository;
	 
	public static void main(String[] args) throws URISyntaxException {
		SpringApplication.run(JobsApplication.class, args);
		
		//FetchJobs obj = new FetchJobs();		
//		try {
			//JSONObject jarray = obj.getJobs();
			/*for(int i=0;i<jarray.size();i++)
			{
				System.out.println(jarray.toString(i));
			}*/
			//obj.StartJob("testjob1");
			//java.util.List<JobStatus> jobs1=obj.CheckStatus(1);
			/*for(int i=0;i<jobs1.size();i++)
	        {
	        	System.err.println("Status od build :1 :"+jobs1.get(i).getBuildstatus());
	        }*/
			//obj.StartJob("Salesforce_retrieve");
		
			
//		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}
	
	@Bean
	SpringResourceTemplateResolver xmlTemplateResolver(ApplicationContext appCtx) {
		SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
		templateResolver.setApplicationContext(appCtx);
		templateResolver.setPrefix("classpath:/templates/");
		templateResolver.setSuffix(".xml");
		templateResolver.setTemplateMode(TemplateMode.XML);
		templateResolver.setCacheable(false);
		templateResolver.setCharacterEncoding("UTF-8");
		return templateResolver;
	}
	
	@Bean(name="springTemplateEngine")
	SpringTemplateEngine templateEngine(ApplicationContext appCtx) {
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolver(xmlTemplateResolver(appCtx));
		return templateEngine;
	}
}
