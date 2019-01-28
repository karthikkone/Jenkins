/*package com.jenkinsjobs.Jobs;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildResult;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.offbytwo.jenkins.model.QueueItem;
import com.offbytwo.jenkins.model.QueueReference;

import net.sf.json.JSONObject;

public class MyCallable implements Callable<String> {
	public static JenkinsServer jenkins;	
	public String BuildName;
	 private static QueueReference queueRef;
	 private static QueueItem queueItem;	 
	 private static final Long DEFAULT_RETRY_INTERVAL = 200L;
    public MyCallable(JenkinsServer jenkins,String BuildName) {
		this.BuildName = BuildName;
		this.jenkins = jenkins;
	}
      
	@Override
    public String call() throws Exception {
		JobWithDetails jobinfo = jenkins.getJob(this.BuildName);		
		queueRef=jobinfo.build(true);		
		queueItem = jenkins.getQueueItem(queueRef);
		System.out.println("queue item 1:"+queueItem);	
		JSONObject jsonobj = new JSONObject();				
		while (queueItem.getExecutable() == null) {		
		       Thread.sleep(DEFAULT_RETRY_INTERVAL);
		       queueItem = jenkins.getQueueItem(queueRef);
		      
		}
		Build build = jenkins.getBuild(queueItem);	
		/*System.out.println("queue item 2:"+queueItem);			
		while(build.details().getResult() == null)
		{
			continue;
		}
		//String res=build.details().getResult().toString();
		//return res;
		return null;
    }
    
    public static void main(String args[]) throws Exception{    	   	
        //Get ExecutorService from Executors utility class, thread pool size is 10
        ExecutorService executor = Executors.newFixedThreadPool(1);
        //create a list to hold the Future object associated with Callable
        List<Future<String>> list = new ArrayList<Future<String>>();
        //Create MyCallable instance
        jenkins = new JenkinsServer(new URI("http://localhost:8080/"), "kit", "kit");
        Callable<String> callable = new MyCallable(jenkins,"testjob2");
        //for(int i=0; i< 1; i++){
            //submit Callable tasks to be executed by thread pool
            Future<String> future = executor.submit(callable);
            //add Future to the list, we can get return value using Future
            list.add(future);
        //}
        for(Future<String> fut : list){
            try {
                //print the return value of Future, notice the output delay in console
                // because Future.get() waits for task to get completed
                System.out.println(new Date()+ "::"+fut.get());
            	//System.out.println(new Date()+ "::"+future);
            } 
            //catch (InterruptedException | ExecutionException e) {
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        //shut down the executor service now
        if(!list.isEmpty())
        {
        executor.shutdown();
        }
    }

}*/
