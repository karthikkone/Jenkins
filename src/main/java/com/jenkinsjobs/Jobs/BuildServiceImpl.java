/*package com.jenkinsjobs.Jobs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BuildServiceImpl implements BuildService{
    @Autowired
    private JobStatusRepo JobsRepo;
	@Override
	public JobStatus createBuild(JobStatus job) {
		// TODO Auto-generated method stub
		//return JobsRepo.save(job);
		return JobsRepo.saveAndFlush(job);
	}

	@Override
	public JobStatus getbuild(Long buildid) {
		// TODO Auto-generated method stub
		return JobsRepo.getOne(buildid);
	}

	@Override
	public JobStatus updateBuild(JobStatus job) {
		// TODO Auto-generated method stub
		//return JobsRepo.save(job);
		return JobsRepo.saveAndFlush(job);
	}

	@Override
	public long getCount() {
		// TODO Auto-generated method stub
		return JobsRepo.count();
	}
	

}
*/
