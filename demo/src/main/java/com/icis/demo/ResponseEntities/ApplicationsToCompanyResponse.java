package com.icis.demo.ResponseEntities;

public class ApplicationsToCompanyResponse {
    private int applicationId;
    private String studentName;
    private String studentSurname;

    public ApplicationsToCompanyResponse(int applicationId,String studentName, String studentSurname) {
        this.applicationId = applicationId;
        this.studentName = studentName;
        this.studentSurname = studentSurname;
    }

    public ApplicationsToCompanyResponse() {
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentSurname() {
        return studentSurname;
    }

    public void setStudentSurname(String studentSurname) {
        this.studentSurname = studentSurname;
    }

}
