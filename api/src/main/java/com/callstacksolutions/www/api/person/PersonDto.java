package com.callstacksolutions.www.api.person;

public class PersonDto {

    private Long id;
    private String fullName;
    private String jobTitle;

    private PersonDto() {}

    public PersonDto(Long id) {
        this.id = id;
    }

    public Long getId() {
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
