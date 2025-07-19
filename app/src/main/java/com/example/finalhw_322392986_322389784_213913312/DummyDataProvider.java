package com.example.finalhw_322392986_322389784_213913312;

import com.example.finalhw_322392986_322389784_213913312.logic_model.Activity;
import com.example.finalhw_322392986_322389784_213913312.logic_model.Guide;
import com.example.finalhw_322392986_322389784_213913312.logic_model.Student;

import java.util.*;

public class DummyDataProvider {

    public static List<Activity> getDummyActivities() {
        List<Activity> activities = new ArrayList<>();

        return activities;
    }

    private static Student cachedStudent;

    public static Student getDummyStudent() {
        if (cachedStudent == null) {
            cachedStudent = new Student();
            cachedStudent.setUid("student001");
            cachedStudent.setFullName("Dana Levi");
            cachedStudent.setEmail("dana@student.com");
            cachedStudent.setType("Student");
            cachedStudent.setAge(14);
            cachedStudent.setFreeDays(Arrays.asList("Wednesday", "Monday", "Tuesday"));
            cachedStudent.setRegisteredActivityIds(Arrays.asList("A1"));

            // Joined activity date
            Map<String, Date> joinedDates = new HashMap<>();
            joinedDates.put("A1", getDaysAgo(9)); // Joined 9 days ago
            cachedStudent.setJoinedActivityDates(joinedDates);

            // Initialize ratings & comments
            cachedStudent.setRatingsByActivity(new HashMap<>());
            cachedStudent.setComments(new HashMap<>());
        }
        return cachedStudent;
    }

    public static List<Student> getDummyStudents() {
        List<Student> students = new ArrayList<>();

        Student s1 = getDummyStudent();

        Student s2 = new Student();
        s2.setUid("student002");
        s2.setFullName("Rami Cohen");
        s2.setEmail("rami@student.com");
        s2.setType("Student");
        s2.setAge(13);
        s2.setFreeDays(Arrays.asList("Tuesday", "Wednesday"));
        s2.setRegisteredActivityIds(Arrays.asList("A1"));

        Map<String, Date> joined = new HashMap<>();
        joined.put("A1", getDaysAgo(8));
        s2.setJoinedActivityDates(joined);

        s2.setRatingsByActivity(new HashMap<>());
        s2.setComments(new HashMap<>());

        students.add(s1);
        students.add(s2);
        return students;
    }

    public static void resetDummyStudent() {
        cachedStudent = null;
    }

    public static Guide getDummyGuide() {
        Guide g = new Guide();
        g.setUid("guide123");
        g.setFullName("Dr. Amir Gal");
        g.setEmail("amir@guide.com");
        g.setType("Guide");
        g.setExpertiseArea("Engineering");
        g.setAssignedActivityIds(Arrays.asList("A1"));
        return g;
    }

    // === Utility ===
    private static Date getDaysAgo(int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -days);
        return cal.getTime();
    }

    private static Date getDaysFromNow(int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, days);
        return cal.getTime();
    }
}
