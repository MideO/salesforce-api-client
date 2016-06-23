package com.mideo.salesforce;

import com.sforce.async.AsyncApiException;
import com.sforce.async.ContentType;
import com.sforce.async.JobInfo;
import com.sforce.async.OperationEnum;


class Job {

    private JobInfo jobInfo;
    private SalesforceConnectionClient salesforceConnectionClient;

    Job(){

        jobInfo = new JobInfo();
    }

    Job withSalesforceClient(SalesforceConnectionClient salesforceConnectionClient){
        this.salesforceConnectionClient = salesforceConnectionClient;
        return this;
    }

    Job newJob(String jobName) {
        jobInfo.setObject(jobName);
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

    JobInfo finishJob() throws AsyncApiException {
        return salesforceConnectionClient
                .getSalesForceWebServiceBulkConnection()
                .closeJob(jobInfo.getId());

    }
}
