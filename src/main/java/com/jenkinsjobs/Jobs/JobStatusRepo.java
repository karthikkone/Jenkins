package com.jenkinsjobs.Jobs;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jenkinsjobs.model.JobStatus;

public interface JobStatusRepo extends JpaRepository<JobStatus, Long> {
}