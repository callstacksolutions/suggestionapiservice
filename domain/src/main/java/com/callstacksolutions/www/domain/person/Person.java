package com.callstacksolutions.www.domain.person;

public class Person {

    private Long id;
    private String fullName;
    private String jobTitle;

    private Person() {}

    public Person(Long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }
}
