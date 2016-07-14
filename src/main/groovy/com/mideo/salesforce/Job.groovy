package com.mideo.salesforce;

import com.sforce.async.*;


class Job {

    def jobInfo = new JobInfo();
    def salesforceConnectionClient;

    Job newJobInfo(String jobName) {
        jobInfo = new JobInfo();
        jobInfo.setObject(jobName);
        return this;
    }

    Job withParallelConcurrencyMode(){
        jobInfo.setConcurrencyMode(ConcurrencyMode.Parallel);
        return this;
    }


    Job setOperation(OperationEnum operationEnum) {
        jobInfo.setOperation(operationEnum);
        return this;
    }

    Job setContentType(ContentType setContentType) {
        jobInfo.setContentType(setContentType);
        return this;
    }

    JobInfo create() throws AsyncApiException {
        jobInfo = salesforceConnectionClient
                .getSalesForceWebServiceBulkConnection()
                .createJob(jobInfo);
        return jobInfo;
    }


    Job toInsert(ContentType contentType) {
        jobInfo.setOperation(OperationEnum.insert);
        jobInfo.setContentType(contentType);
        return this;
    }

    Job toQuery(ContentType contentType) {
        jobInfo.setOperation(OperationEnum.query);
        jobInfo.setContentType(contentType);
        return this;
    }
}
