package org.bobpark.transcoder.domain.job.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.bobpark.transcoder.domain.job.entity.Job;

public interface JobRepository extends JpaRepository<Job, Long> {
}
