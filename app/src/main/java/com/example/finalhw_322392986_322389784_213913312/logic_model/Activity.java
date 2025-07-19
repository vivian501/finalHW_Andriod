package com.example.finalhw_322392986_322389784_213913312.logic_model;

import java.util.Date;
import java.util.List;

public class Activity {
   private String activityId;
   private String name;
   private String domain;

   private String subDomain;
   private String description;
   private String ageRange; // is this good or should i do min max age??
   private int minAge;
   private int maxAge;
   private List<String> days;
   private int maxParticipants;
   private List<String> registeredUserIds; //to store all the users signed for an activity
   private String guideId; // to save the guide that conducts the activity
   private String guideFullName;
   private Date startDate; //when the activity starts
   private Date EndDate; //when the activity ends
   private List<String> Photos;


    // constructor, empty
    public Activity() {
    }

    public int getMinAge() {
        return minAge;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomain() {
        return domain;
    }
    public void setSubDomain() {  this.subDomain=subDomain;}
    public String getSubDomain() { return subDomain;}

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(String ageRange) {
        this.ageRange = ageRange;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }



    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public String getActivityId() {

        return activityId;
    }

    public void setActivityId(String activityId) {

        this.activityId = activityId;
    }

    public List<String> getRegisteredUserIds() {

        return registeredUserIds;
    }

    public void setRegisteredUserIds(List<String> registeredUserIds) {
        this.registeredUserIds = registeredUserIds;
    }

    public String getGuideId() {

        return guideId;
    }

    public void setGuideId(String guideId) {

        this.guideId = guideId;
    }


    public String getGuideFullName() {
        return guideFullName;
    }

    public void setGuideFullName(String guideFullName) {
        this.guideFullName = guideFullName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return EndDate;
    }

    public void setEndDate(Date endDate) {
        EndDate = endDate;
    }

    public List<String> getPhotos() {
        return Photos;
    }

    public void setPhotos(List<String> photos) {
        Photos = photos;
    }

    public List<String> getDays() {
        return days;
    }

    public void setDays(List<String> days) {
        this.days = days;
    }
}
