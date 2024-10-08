package org.bobpark.transcoder.domain.job.repository.query.impl;

import static org.bobpark.transcoder.domain.job.entity.QJob.*;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import org.bobpark.transcoder.domain.job.entity.Job;
import org.bobpark.transcoder.domain.job.entity.QJob;
import org.bobpark.transcoder.domain.job.model.SearchJobRequest;
import org.bobpark.transcoder.domain.job.repository.query.JobQueryRepository;
import org.bobpark.transcoder.domain.job.type.JobStatus;
import org.bobpark.transcoder.domain.job.type.JobType;

@RequiredArgsConstructor
public class JobQueryRepositoryImpl implements JobQueryRepository {

    private final JPAQueryFactory query;

    @Override
    public Job fetch() {
        return query.selectFrom(job)
            .where(job.status.in(JobStatus.WAITING, JobStatus.PROCEEDING))
            .orderBy(job.createdDate.asc())
            .fetchFirst();
    }

    @Override
    public Page<Job> search(SearchJobRequest searchRequest, Pageable pageable) {

        List<Job> content =
            query.selectFrom(job)
                .where(mappingCondition(searchRequest))
                .orderBy(job.createdDate.asc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        JPAQuery<Long> countQuery =
            query.select(job.id.count())
                .from(job)
                .where(mappingCondition(searchRequest));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private Predicate mappingCondition(SearchJobRequest searchRequest) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(eqType(searchRequest.type()))
            .and(eqStatus(searchRequest.status()));

        return builder;
    }

    private BooleanExpression eqType(JobType type) {
        return type != null ? job.type.eq(type) : null;
    }

    private BooleanExpression eqStatus(JobStatus status) {
        return status != null ? job.status.eq(status) : null;
    }
}
