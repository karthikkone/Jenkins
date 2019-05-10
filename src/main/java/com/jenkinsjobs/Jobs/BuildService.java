package com.jenkinsjobs.Jobs;

import com.jenkinsjobs.model.JobStatus;

public interface BuildService {
    JobStatus createBuild(JobStatus job);
    JobStatus getbuild(Long buildid);
    JobStatus updateBuild(JobStatus job);
    long getCount();
}

