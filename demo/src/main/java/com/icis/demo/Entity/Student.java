package com.icis.demo.Entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "student")
public class Student {
    @Id
    @Column(name = "id")
    private int id;
    @Column(name = "department_id")
    private int departmentId;
    @Column(name = "namee")
    private String name;
    @Column(name = "surname")
    private String surname;
    @Column(name = "passwordd")
    private String password;
    @Column(name = "last_access_time")
    private Date lastAccessTime;
    @Column(name = "grade")
    private int grade;
    @Column(name = "email")
    private String email;
    @Column(name = "is_foreign")
    private boolean isForeign;
    @Column(name = "citizen_id")
    private String citizenId;
    @Column(name = "telephone")
    private String telephone;

    public Student(int departmentId, String name, String surname, String password, Date lastAccessTime, int grade,
                   String email, boolean isForeign, String citizenId, String telephone) {
        this.departmentId = departmentId;
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.lastAccessTime = lastAccessTime;
        this.grade = grade;
        this.email = email;
        this.isForeign = isForeign;
        this.citizenId = citizenId;
        this.telephone = telephone;
    }

    public Student() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(Date lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isForeign() {
        return isForeign;
    }

    public void setIsForeign(boolean isForeign) {
        this.isForeign = isForeign;
    }

    public String getCitizenId() {
        return citizenId;
    }

    public void setCitizenId(String citizenId) {
        this.citizenId = citizenId;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
