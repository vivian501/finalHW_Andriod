package com.example.finalhw_322392986_322389784_213913312.logic_model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Coordinator extends User implements Serializable {
    private List<String> managedActivityIds; // Activities created by the coordinator
    private Map<String, String> assignedGuideMap; // Key: activityId, Value: guideId (if you're managing assignments inside the coordinator)

    public Coordinator() {
        // Required for Firebase
    }

    public Coordinator(String uid, String fullName, String email, String role) {
        super(uid, fullName, email, role);
    }

    public List<String> getManagedActivityIds() {
        return managedActivityIds;
    }

    public void setManagedActivityIds(List<String> managedActivityIds) {
        this.managedActivityIds = managedActivityIds;
    }

    public Map<String, String> getAssignedGuideMap() {
        return assignedGuideMap;
    }

    public void setAssignedGuideMap(Map<String, String> assignedGuideMap) {
        this.assignedGuideMap = assignedGuideMap;
    }
}
