# salesforce-api-client

###Build Status
[![Build Status](https://travis-ci.org/MideO/salesforce-api-client.svg?branch=master)](https://travis-ci.org/MideO/salesforce-api-client)


###Setup dependency

#### In build.gradle

#####Add maven url
```groovy
repositories {
    ....
    maven {url 'https://github.com/MideO/salesforce-api-client/raw/mvn-repo/'}
}
```
   
#####Add dependency
```groovy
dependencies {
    ....
    compile group: 'com.mideo', name:'salesforce-api-client', version: '0.0.1'
}
```


#####Usage
```Java
SalesforceConfig config = new SalesforceConfig("https://test.salesforce.com")
                .clientId("dfghjkl")
                .clientSecret("fdghjkl;")
                .userName("a@b.com")
                .userToken("gfhjk")
                .password("fghjkl");
RequestSpecification requestSpecification = HttpRequestSpecificationBuilder.build();
SalesforceConnectionClient connectionClient = new SalesforceConnectionClient(config, requestSpecification);
SalesforceWebServiceClient webClient = new SalesforceWebServiceClient(connectionClient);


//Create sObject
Map<String,Object> logData = new HashMap<>();
logData.put("Short_Description__c","Api test"+ DateTime.now().toString());
logData.put("Description__c","test description Api test"+ DateTime.now().toString()) 
String result = webClient.createObject("Log__c", logData);

            
//Update sObject
logData.put("Exception_Type__c","dummyEx"+ DateTime.now().toString());
result = webClient.updateObject("Log__c",result, logData);


//Retrieve Object
Map<String, Object> resultMap = webClient.retrieveObject("Case", caseId);
            
            
//Delete sObject
String result = webClient.deleteObject(contactId);

                        
//Execute Anonymous Apex 
ExecuteAnonymousResult exectueResult = webClient.executeApexBlock("System.debug('test debug message');");


//Export data
List<Map<String, String>> dataList = webClient
                                    .setPublishStatusCheckTimeout(10000)
                                    .exportDataFromTable("Account");


//Publish csv stream to sObject via bulk api
PublishResult publishResult = webClient.publishCsvToTable(csvInputStream, "Contact");

//Get published data status
String status = getPublishedDataStatus(
                        publishResult.jobInfo.getId, 
                        publishResult.batchInfo.getId
                            );
            
//Export filtered data
Map<String,String> filter = new HashMap<>();
filter.put("Product__c","DummyBox");
filter.put("Delivered",true);
List<Map<String, String>> dataList = webClient.exportDataFromTable("Order__c", filter);


//Export filtered data columns
Map<String,String> filter = new HashMap<>();
filter.put("Product__c","DummyBox");
filter.put("Delivered",true);
List<String> columns = new ArrayList<>();
columns.add("Short_Description__c");
columns.add("Description__c");
List<Map<String, String>> dataList = webClient.exportDataFromTable("Order__c", columns, filter);

```