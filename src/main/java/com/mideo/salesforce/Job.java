package com.mideo.salesforce;

import com.sforce.async.*;


class Job {

    private JobInfo jobInfo;

    Job(){
        jobInfo = new JobInfo();
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

    JobInfo create(SalesforceConnectionClient salesforceConnectionClient) throws AsyncApiException {
        jobInfo = salesforceConnectionClient.getSalesForceWebServiceBulkConnection().createJob(jobInfo);
        return jobInfo;
    }

    boolean finishJob(SalesforceConnectionClient salesforceConnectionClient) throws AsyncApiException {
        salesforceConnectionClient.getSalesForceWebServiceBulkConnection().closeJob(jobInfo.getId());
        return true;
    }
}
