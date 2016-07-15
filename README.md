# salesforce-api-client

###Build Status
[![Build Status](https://travis-ci.org/MideO/salesforce-api-client.svg?branch=master)](https://travis-ci.org/MideO/salesforce-api-client)


###Adding dependencies

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

SalesforceConnectionClient connectionClient = new SalesforceConnectionClient(config, HttpRequestSpecificationBuilder.build());
SalesforceWebServiceClient webClient = new SalesforceWebServiceClient(connectionClient);

List<Map<String, String>> dataList = webClient.exportDataFromTable("Account");
```