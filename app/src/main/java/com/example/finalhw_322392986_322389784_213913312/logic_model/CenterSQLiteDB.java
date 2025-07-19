
package com.example.finalhw_322392986_322389784_213913312.logic_model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.*;

public class CenterSQLiteDB extends SQLiteOpenHelper {

    // Constants for database name and version
    private static final String DATABASE_NAME = "center.db";
    private static final int DATABASE_VERSION = 1;

    // user table
    private static final String TABLE_USERS = "users";

    // activity table
    private static final String TABLE_ACTIVITIES = "activities";


    // columns for users table
    private static final String USER_COLUMN_UID = "uid";
    private static final String USER_COLUMN_FULL_NAME = "fullName";
    private static final String USER_COLUMN_EMAIL = "email";
    private static final String USER_COLUMN_TYPE = "type";
    private static final String USER_COLUMN_AGE = "age";
    private static final String USER_COLUMN_FREE_DAYS = "freeDays";
    private static final String USER_COLUMN_REGISTERED_IDS = "registeredActivityIds";
    private static final String USER_COLUMN_JOINED_DATES = "joinedActivityDates";
    private static final String USER_COLUMN_RATINGS = "ratingsByActivity";
    private static final String USER_COLUMN_COMMENTS = "comments";
    private static final String USER_COLUMN_EXPERTISE = "expertiseArea";
    private static final String USER_COLUMN_ASSIGNED_IDS = "assignedActivityIds";

    // columns for activities table
    private static final String ACTIVITY_COLUMN_ID = "activityId";
    private static final String ACTIVITY_COLUMN_NAME = "name";
    private static final String ACTIVITY_COLUMN_DOMAIN = "domain";
    private static final String ACTIVITY_COLUMN_DESCRIPTION = "description";
    private static final String ACTIVITY_COLUMN_AGE_RANGE = "ageRange";
    private static final String ACTIVITY_COLUMN_MIN_AGE = "minAge";
    private static final String ACTIVITY_COLUMN_MAX_AGE = "maxAge";
    private static final String ACTIVITY_COLUMN_DAYS = "days";
    private static final String ACTIVITY_COLUMN_MAX_PARTICIPANTS = "maxParticipants";
    private static final String ACTIVITY_COLUMN_REGISTERED_IDS = "registeredUserIds";
    private static final String ACTIVITY_COLUMN_GUIDE_ID = "guideId";
    private static final String ACTIVITY_COLUMN_GUIDE_NAME = "guideFullName";
    private static final String ACTIVITY_COLUMN_START_DATE = "startDate";
    private static final String ACTIVITY_COLUMN_END_DATE = "endDate";
    private static final String ACTIVITY_COLUMN_PHOTOS = "photos";

    private SQLiteDatabase db;

    public CenterSQLiteDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String CREATE_ACTIVITIES_TABLE = "create table if not exists activities ( " +
                    "activityId TEXT PRIMARY KEY, " +
                    "name TEXT, " +
                    "domain TEXT, " +
                    "description TEXT, " +
                    "ageRange TEXT, " +
                    "minAge INTEGER, " +
                    "maxAge INTEGER, " +
                    "days TEXT, " +
                    "maxParticipants INTEGER, " +
                    "registeredUserIds TEXT, " +
                    "guideId TEXT, " +
                    "guideFullName TEXT, " +
                    "startDate INTEGER, " +
                    "endDate INTEGER, " +
                    "photos TEXT)";
            db.execSQL(CREATE_ACTIVITIES_TABLE);

            String CREATE_USERS_TABLE = "create table if not exists users ( " +
                    "uid TEXT PRIMARY KEY, " +
                    "fullName TEXT, " +
                    "email TEXT, " +
                    "type TEXT, " +
                    "age INTEGER, " +
                    "freeDays TEXT, " +
                    "registeredActivityIds TEXT, " +
                    "joinedActivityDates TEXT, " +
                    "ratingsByActivity TEXT, " +
                    "comments TEXT, " +
                    "expertiseArea TEXT, " +
                    "assignedActivityIds TEXT)";
            db.execSQL(CREATE_USERS_TABLE);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public void open() {
        try {
            db = getWritableDatabase();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void close() {
        try {
            db.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }



    public List<Activity> getAllActivities() {
        List<Activity> list = new ArrayList<>();
        Cursor cursor = db.query(TABLE_ACTIVITIES, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            Activity a = new Activity();
            a.setActivityId(cursor.getString(0));
            a.setName(cursor.getString(1));
            a.setDomain(cursor.getString(2));
            a.setDescription(cursor.getString(3));
            a.setAgeRange(cursor.getString(4));
            a.setMinAge(cursor.getInt(5));
            a.setMaxAge(cursor.getInt(6));
            a.setDays(splitList(cursor.getString(7)));
            a.setMaxParticipants(cursor.getInt(8));
            a.setRegisteredUserIds(splitList(cursor.getString(9)));
            a.setGuideId(cursor.getString(10));
            a.setGuideFullName(cursor.getString(11));
            a.setStartDate(new Date(cursor.getLong(12)));
            a.setEndDate(new Date(cursor.getLong(13)));
            a.setPhotos(splitList(cursor.getString(14)));
            list.add(a);
        }
        cursor.close();
        return list;
    }


    public void deleteAllActivities() {
        try {

            db.delete(TABLE_ACTIVITIES, null, null);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    // Add a new activity
    public void addActivity(Activity a) {

        try {
            ContentValues values = new ContentValues();
            values.put("activityId", a.getActivityId());
            values.put("name", a.getName());
            values.put("domain", a.getDomain());
            values.put("description", a.getDescription());
            values.put("ageRange", a.getAgeRange());
            values.put("minAge", a.getMinAge());
            values.put("maxAge", a.getMaxAge());
            values.put("days", joinList(a.getDays()));
            values.put("maxParticipants", a.getMaxParticipants());
            values.put("registeredUserIds", joinList(a.getRegisteredUserIds()));
            values.put("guideId", a.getGuideId());
            values.put("guideFullName", a.getGuideFullName());
            values.put("startDate", a.getStartDate() != null ? a.getStartDate().getTime() : 0);
            values.put("endDate", a.getEndDate() != null ? a.getEndDate().getTime() : 0);
            values.put("photos", joinList(a.getPhotos()));

            db.insert(TABLE_ACTIVITIES, null, values);
        } catch (Throwable t) {
            t.printStackTrace();

        }
    }

    // Update an existing activity
    public void updateActivity(Activity a) {

        try {
            ContentValues values = new ContentValues();
            values.put("name", a.getName());
            values.put("domain", a.getDomain());
            values.put("description", a.getDescription());
            values.put("ageRange", a.getAgeRange());
            values.put("minAge", a.getMinAge());
            values.put("maxAge", a.getMaxAge());
            values.put("days", joinList(a.getDays()));
            values.put("maxParticipants", a.getMaxParticipants());
            values.put("registeredUserIds", joinList(a.getRegisteredUserIds()));
            values.put("guideId", a.getGuideId());
            values.put("guideFullName", a.getGuideFullName());
            values.put("startDate", a.getStartDate() != null ? a.getStartDate().getTime() : 0);
            values.put("endDate", a.getEndDate() != null ? a.getEndDate().getTime() : 0);
            values.put("photos", joinList(a.getPhotos()));

            db.update(TABLE_ACTIVITIES, values, "activityId = ?", new String[]{a.getActivityId()});
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    // Add a new user
    public void addUser(User user) {

        try {
            ContentValues values = new ContentValues();
            values.put("uid", user.getUid());
            values.put("fullName", user.getFullName());
            values.put("email", user.getEmail());
            values.put("type", user.getType());

            if (user instanceof Student) {
                Student s = (Student) user;
                values.put("age", s.getAge());
                values.put("freeDays", joinList(s.getFreeDays()));
                values.put("registeredActivityIds", joinList(s.getRegisteredActivityIds()));
                values.put("joinedActivityDates", serializeMapDate(s.getJoinedActivityDates()));
                values.put("ratingsByActivity", serializeMapInt(s.getRatingsByActivity()));
                values.put("comments", serializeMapString(s.getComments()));
            }

            if (user instanceof Guide) {
                Guide g = (Guide) user;
                values.put("expertiseArea", g.getExpertiseArea());
                values.put("assignedActivityIds", joinList(g.getAssignedActivityIds()));
            }

            db.insert(TABLE_USERS, null, values);

        } catch (Throwable t) {
            t.printStackTrace();

        }
    }

    // Update an existing user
    public void updateUser(User user) {

        try {
            ContentValues values = new ContentValues();
            values.put("fullName", user.getFullName());
            values.put("email", user.getEmail());
            values.put("type", user.getType());

            if (user instanceof Student) {
                Student s = (Student) user;
                values.put("age", s.getAge());
                values.put("freeDays", joinList(s.getFreeDays()));
                values.put("registeredActivityIds", joinList(s.getRegisteredActivityIds()));
                values.put("joinedActivityDates", serializeMapDate(s.getJoinedActivityDates()));
                values.put("ratingsByActivity", serializeMapInt(s.getRatingsByActivity()));
                values.put("comments", serializeMapString(s.getComments()));
            }

            if (user instanceof Guide) {
                Guide g = (Guide) user;
                values.put("expertiseArea", g.getExpertiseArea());
                values.put("assignedActivityIds", joinList(g.getAssignedActivityIds()));
            }

            db.update(TABLE_USERS, values, "uid = ?", new String[]{user.getUid()});
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }


    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        Cursor c = db.query(TABLE_USERS, null, null, null, null, null, null);
        while (c.moveToNext()) {
            String type = c.getString(3);
            if ("student".equalsIgnoreCase(type)) {
                Student s = new Student();
                s.setUid(c.getString(0));
                s.setFullName(c.getString(1));
                s.setEmail(c.getString(2));
                s.setType(type);
                s.setAge(c.getInt(4));
                s.setFreeDays(splitList(c.getString(5)));
                s.setRegisteredActivityIds(splitList(c.getString(6)));
                s.setJoinedActivityDates(deserializeMapDate(c.getString(7)));
                s.setRatingsByActivity(deserializeMapInt(c.getString(8)));
                s.setComments(deserializeMapString(c.getString(9)));
                list.add(s);
            } else if ("guide".equalsIgnoreCase(type)) {
                Guide g = new Guide();
                g.setUid(c.getString(0));
                g.setFullName(c.getString(1));
                g.setEmail(c.getString(2));
                g.setType(type);
                g.setExpertiseArea(c.getString(10));
                g.setAssignedActivityIds(splitList(c.getString(11)));
                list.add(g);
            } else {  //this needs to be updated for each of the other user types!
                User u = new User();
                u.setUid(c.getString(0));
                u.setFullName(c.getString(1));
                u.setEmail(c.getString(2));
                u.setType(type);
                list.add(u);
            }
        }
        c.close();
        return list;
    }

    public void deleteAllUsers() {
        try {

            db.delete(TABLE_USERS, null, null);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    // Helpers to join/split lists into a single string for storage
    private String joinList(List<String> list) {
        return list != null ? String.join(",", list) : "";
    }

    private List<String> splitList(String str) {
        return str != null && !str.isEmpty() ? Arrays.asList(str.split(",")) : new ArrayList<>();
    }

    // Convert Map<String, Date> to a string (key=timestamp;)
    private String serializeMapDate(Map<String, Date> map) {
        if (map == null) return "";
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Date> e : map.entrySet()) {
            sb.append(e.getKey()).append("=").append(e.getValue().getTime()).append(";");
        }
        return sb.toString();
    }

    // Convert back from string to Map<String, Date>
    private Map<String, Date> deserializeMapDate(String str) {
        Map<String, Date> map = new HashMap<>();
        if (str == null || str.isEmpty()) return map;
        for (String entry : str.split(";")) {
            String[] parts = entry.split("=");
            map.put(parts[0], new Date(Long.parseLong(parts[1])));
        }
        return map;
    }

    // Convert Map<String, String> to string
    private String serializeMapInt(Map<String, Integer> map) {
        if (map == null) return "";
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> e : map.entrySet()) {
            sb.append(e.getKey()).append("=").append(e.getValue()).append(";");
        }
        return sb.toString();
    }

    // Convert back from string to Map<String, Date>
    private Map<String, Integer> deserializeMapInt(String str) {
        Map<String, Integer> map = new HashMap<>();
        if (str == null || str.isEmpty()) return map;
        for (String entry : str.split(";")) {
            String[] parts = entry.split("=");
            map.put(parts[0], Integer.parseInt(parts[1]));
        }
        return map;
    }

    // Convert Map<String, String> to string
    private String serializeMapString(Map<String, String> map) {
        if (map == null) return "";
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> e : map.entrySet()) {
            sb.append(e.getKey()).append("=").append(e.getValue()).append(";");
        }
        return sb.toString();
    }

    // Convert back from string to Map<String, Date>
    private Map<String, String> deserializeMapString(String str) {
        Map<String, String> map = new HashMap<>();
        if (str == null || str.isEmpty()) return map;
        for (String entry : str.split(";")) {
            String[] parts = entry.split("=");
            map.put(parts[0], parts[1]);
        }
        return map;
    }

    // a method to delete an activity by its id
    public void deleteActivity(Activity item) {

        try {

            // delete item
            db.delete(TABLE_ACTIVITIES, ACTIVITY_COLUMN_ID + " = ?",
                    new String[] { item.getActivityId() });
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    // a method to delete a user by his id
    public void deleteUser(User item) {

        try {

            //delete item
            db.delete(TABLE_USERS, USER_COLUMN_UID + " = ?",
                    new String[]{item.getUid()});
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}