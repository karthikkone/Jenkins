package com.jenkinsjobs.Jobs;

public interface BuildService {
    JobStatus createBuild(JobStatus job);
    JobStatus getbuild(Long buildid);
    JobStatus updateBuild(JobStatus job);
    long getCount();
}

