package com.example.finalhw_322392986_322389784_213913312.logic_model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Student extends User implements Serializable{
    private int age;
    private List<String> registeredActivityIds;
    private Map<String, Integer> ratingsByActivity; // Key: activityId, Value: rating (1–10) to save the rating
    private List<String> freeDays;
    private Map<String, Date> joinedActivityDates; // this is going to be used to save the join date of each activity key: activityId, value: join date
    private Map<String, String> comments; // this map stors the comments the guides leave for this student (key activity id, value comment)


    public Student() {
    }


    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<String> getRegisteredActivityIds() {
        return registeredActivityIds;
    }

    public void setRegisteredActivityIds(List<String> registeredActivityIds) {
        this.registeredActivityIds = registeredActivityIds;
    }

    public Student(String uid, String fullName, String email, String role, int age) {
        super(uid, fullName, email, role);
        this.age = age;
    }

    public List<String> getFreeDays() {
        return freeDays;
    }

    public void setFreeDays(List<String> freeDays) {
        this.freeDays = freeDays;
    }

    public Map<String, Date> getJoinedActivityDates() {
        return joinedActivityDates;
    }

    public void setJoinedActivityDates(Map<String, Date> joinedActivityDates) {
        this.joinedActivityDates = joinedActivityDates;
    }

    public Map<String, Integer> getRatingsByActivity() {
        return ratingsByActivity;
    }

    public void setRatingsByActivity(Map<String, Integer> ratingsByActivity) {
        this.ratingsByActivity = ratingsByActivity;
    }

    public Map<String, String> getComments() {
        return comments;
    }

    public void setComments(Map<String, String> comments) {
        this.comments = comments;
    }
}
