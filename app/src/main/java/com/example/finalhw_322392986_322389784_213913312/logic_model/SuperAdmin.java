package com.example.finalhw_322392986_322389784_213913312.logic_model;

import java.io.Serializable;
import java.util.List;

public class SuperAdmin extends User implements Serializable {
    private List<String> flaggedActivityIds;  // Activities that were flagged (bad feedback)
    private List<String> statusUpdatedActivityIds; // Activities whose status was updated by this admin

    public SuperAdmin() {
        // Required for Firebase deserialization
    }

    public SuperAdmin(String uid, String fullName, String email, String role) {
        super(uid, fullName, email, role);
    }

    public List<String> getFlaggedActivityIds() {
        return flaggedActivityIds;
    }

    public void setFlaggedActivityIds(List<String> flaggedActivityIds) {
        this.flaggedActivityIds = flaggedActivityIds;
    }

    public List<String> getStatusUpdatedActivityIds() {
        return statusUpdatedActivityIds;
    }

    public void setStatusUpdatedActivityIds(List<String> statusUpdatedActivityIds) {
        this.statusUpdatedActivityIds = statusUpdatedActivityIds;
    }
}
