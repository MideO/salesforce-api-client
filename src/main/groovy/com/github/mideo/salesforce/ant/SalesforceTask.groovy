package com.github.mideo.salesforce.ant

import org.apache.tools.ant.Task


public class SalesforceTask extends Task {

    protected String configFileName = 'conf.json';
    protected String csvFilesRelativePath = 'config/customSettings';
    protected String serverUrl;
    protected String userName;
    protected String password;


    public void setConfigFileName(String configFile) {
        this.configFileName = configFile
    }

    public void setCsvFilesRelativePath(String csvLocation) {
        this.csvFilesRelativePath = csvLocation
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl
    }

    public void setUserName(String userName) {
        this.userName = userName
    }

    public void setPassword(String password) {
        this.password = password
    }


}
