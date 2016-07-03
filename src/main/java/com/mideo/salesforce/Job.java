package com.mideo.salesforce;

import com.sforce.async.*;


class Job {

    private JobInfo jobInfo;
    private SalesforceConnectionClient salesforceConnectionClient;


    Job() {
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

    Job forSObject(String sObjectName) {
        jobInfo.setObject(sObjectName);
        return this;
    }

    Job isConcurrent(){
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
}
