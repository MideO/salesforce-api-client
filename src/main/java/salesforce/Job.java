package salesforce;

import com.sforce.async.*;


class Job {

    private JobInfo job;

    public JobInfo getJob() {
        return job;
    }


    Job(){
        job = new JobInfo();
    }

    Job newJob(String jobName) {
        job.setObject(jobName);
        return this;
    }

    Job setOperation(OperationEnum operationEnum) {
        job.setOperation(operationEnum);
        return this;
    }

    Job setContentType(ContentType setContentType) {
        job.setContentType(setContentType);
        return this;
    }

    JobInfo create(ConnectionClient connectionClient) throws AsyncApiException {
        job = connectionClient.getSalesForceWebServiceBulkConnection().createJob(job);
        return job;
    }

    void finishJob(ConnectionClient connectionClient) throws AsyncApiException {
        connectionClient.getSalesForceWebServiceBulkConnection().closeJob(job.getId());
    }
}
