package com.github.mideo.salesforce.ant

import spock.lang.Specification

class SalesforceTaskTest extends Specification {

    def task

    def setup() {
        task = new SalesforceTask();
    }

    def "Should set csvFilesRelativePath"() {
        when:
            task.setCsvFilesRelativePath('abvc')
        then:
            assert task.csvFilesRelativePath == 'abvc'

    }

    def "Should set configFileName"() {
        when:
        task.setConfigFileName('abvc/abc.json')
        then:
        assert task.configFileName == 'abvc/abc.json'

    }



    def "Should set serverUrl"() {
        when:
            task.setServerUrl('bcs.com')
        then:
            assert task.serverUrl == 'bcs.com'
    }

    def "Should set userName"() {
        when:
            task.setUserName('sds')
        then:
            assert task.userName == 'sds'
    }

    def "Should set password"() {

        when:
            task.setPassword('sds')
        then:
            assert task.password == 'sds'
    }

}
