package salesforce;

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

    JobInfo create(ConnectionClient connectionClient) throws AsyncApiException {
        jobInfo = connectionClient.getSalesForceWebServiceBulkConnection().createJob(jobInfo);
        return jobInfo;
    }

    boolean finishJob(ConnectionClient connectionClient) throws AsyncApiException {
        connectionClient.getSalesForceWebServiceBulkConnection().closeJob(jobInfo.getId());
        return true;
    }
}
