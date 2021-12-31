package com.example.mutidemo.bean;

import cn.bmob.v3.BmobUser;

public class UserBean extends BmobUser {
    private String hospitalId;
    private String idCardNumber;
    private Integer height;
    private Float weight;
    private String education;
    private String medicalHistory;
    private String hospitalTime;

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getIdCardNumber() {
        return idCardNumber;
    }

    public void setIdCardNumber(String idCardNumber) {
        this.idCardNumber = idCardNumber;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public String getHospitalTime() {
        return hospitalTime;
    }

    public void setHospitalTime(String hospitalTime) {
        this.hospitalTime = hospitalTime;
    }
}
