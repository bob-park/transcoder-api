package org.bobpark.transcoder.domain.job.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.bobpark.transcoder.domain.job.entity.Job;
import org.bobpark.transcoder.domain.job.repository.query.JobQueryRepository;

public interface JobRepository extends JpaRepository<Job, Long>, JobQueryRepository {
}
