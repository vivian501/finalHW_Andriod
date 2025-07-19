package com.example.finalhw_322392986_322389784_213913312.logic_model;

import java.io.Serializable;
import java.util.List;

public class Parent extends User implements Serializable {

    private List<String> childrenEmails;  // emails entered at signup
    private List<String> childrenIds;     // optional: resolved user UIDs of children

    public Parent() {}

    public Parent(String uid, String fullName, String email, String role, List<String> childrenEmails) {
        super(uid, fullName, email, role);
        this.childrenEmails = childrenEmails;
    }

    public List<String> getChildrenEmails() {
        return childrenEmails;
    }

    public void setChildrenEmails(List<String> childrenEmails) {
        this.childrenEmails = childrenEmails;
    }

    public List<String> getChildrenIds() {
        return childrenIds;
    }

    public void setChildrenIds(List<String> childrenIds) {
        this.childrenIds = childrenIds;
    }
}
