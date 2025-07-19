package com.example.finalhw_322392986_322389784_213913312.logic_model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class CenterDataBaseManager {

    private static CenterDataBaseManager instance = null;
    private Context context = null;
    private CenterSQLiteDB db = null;
    private User loggedInUser = null;

    // Singleton pattern
    public static CenterDataBaseManager getInstance() {
        if (instance == null) {
            instance = new CenterDataBaseManager();
        }
        return instance;
    }

    public static void releaseInstance() {
        if (instance != null) {
            instance.clean();
            instance = null;
        }
    }

    private void clean() {
        loggedInUser = null;
    }

    // Open database
    public void openDataBase(Context context) {
        this.context = context;
        if (context != null) {
            db = new CenterSQLiteDB(context);
            db.open();
        }
    }

    // Close database
    public void closeDataBase() {
        if (db != null) {
            db.close();
        }
    }

    // Add new user
    public void addUser(User user) {
        if (db != null) {
            db.addUser(user);
        }
    }

    // Update existing user
    public void updateUser(User user) {
        if (db != null) {
            db.updateUser(user);
        }
    }

    // Get all users
    public List<User> getAllUsers() {
        if (db != null) {
            return db.getAllUsers();
        }
        return new ArrayList<>();
    }

    // Delete a user
    public void deleteUser(User user) {
        if (db != null) {
            db.deleteUser(user);
        }
    }

    // Add new activity
    public void addActivity(Activity activity) {
        if (db != null) {
            db.addActivity(activity);
        }
    }

    // Update existing activity
    public void updateActivity(Activity activity) {
        if (db != null) {
            db.updateActivity(activity);
        }
    }

    // Get all activities
    public List<Activity> getAllActivities() {
        if (db != null) {
            return db.getAllActivities();
        }
        return new ArrayList<>();
    }

    // Delete an activity
    public void deleteActivity(Activity activity) {
        if (db != null) {
            db.deleteActivity(activity);
        }
    }

    // Remove everything
    public void removeAllData() {
        if (db != null) {
            db.deleteAllUsers();
            db.deleteAllActivities();
        }
    }

    // Getter/setter for logged-in user (for caching or in-memory use)
    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }

    public Context getContext() {
        return context;
    }
}
