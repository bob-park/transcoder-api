package org.bobpark.transcoder.domain.job.repository.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.bobpark.transcoder.domain.job.entity.Job;
import org.bobpark.transcoder.domain.job.model.SearchJobRequest;

public interface JobQueryRepository {

    Job fetch();

    Page<Job> search(SearchJobRequest searchRequest, Pageable pageable);

}
