package com.example.finalhw_322392986_322389784_213913312.logic_model;

import java.util.Date;
import java.util.List;

public class Guide extends User {
    private String expertiseArea;
    private List<String> assignedActivityIds;
    private Date startDate;

    public String getExpertiseArea() {
        return expertiseArea;
    }

    public void setExpertiseArea(String expertiseArea) {
        this.expertiseArea = expertiseArea;
    }

    public List<String> getAssignedActivityIds() {
        return assignedActivityIds;
    }

    public void setAssignedActivityIds(List<String> assignedActivityIds) {
        this.assignedActivityIds = assignedActivityIds;
    }

    public Guide() {

    }

    public Guide(String uid, String fullName, String email, String role, String expertiseArea) {
        super(uid, fullName, email, role);
        this.expertiseArea = expertiseArea;
    }
}
