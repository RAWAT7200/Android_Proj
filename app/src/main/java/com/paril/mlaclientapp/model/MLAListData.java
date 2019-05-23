package com.paril.mlaclientapp.model;

/**
 * Created by paril on 7/11/2017.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class MLAListData {
    String userType;

    public MLAListData(String userType) {
        this.userType = userType;
    }

    public HashMap<String,List<MLAListModel>> getlist()  {

        HashMap <String,List<MLAListModel>> navDrawerHashMap = new LinkedHashMap<String, List<MLAListModel>>();

        if(userType.equals("admin"))
        {
            List<MLAListModel> home = new ArrayList<>();

            List<MLAListModel> myInformation = new ArrayList<>();

            List<MLAListModel> admin = new ArrayList<>();

            List<MLAListModel> instructor = new ArrayList<>();

            List<MLAListModel> student = new ArrayList<MLAListModel>();

            List<MLAListModel> subjects = new ArrayList<>();

            List<MLAListModel> enroll = new ArrayList<MLAListModel>();
            enroll.add(new MLAListModel("Enroll"));
            enroll.add(new MLAListModel("Display"));
            enroll.add(new MLAListModel("Drop"));

            List<MLAListModel> schedule = new ArrayList<MLAListModel>();
            schedule.add(new MLAListModel("In-process "));
            schedule.add(new MLAListModel("Add Schedule"));
            schedule.add(new MLAListModel("Display Schedule"));
            schedule.add(new MLAListModel("Edit Schedule"));
            schedule.add(new MLAListModel("Remove Schedule"));

            List<MLAListModel> grade = new ArrayList<>();

            List<MLAListModel> calendar = new ArrayList<>();

            List<MLAListModel> messages = new ArrayList<>();

            List<MLAListModel> updatePassword = new ArrayList<>();

            List<MLAListModel> logout = new ArrayList<>();


            navDrawerHashMap.put("Home", home);
            navDrawerHashMap.put("My Info",myInformation);
            navDrawerHashMap.put("Admin", admin);
            navDrawerHashMap.put("Instructor", instructor);
            navDrawerHashMap.put("Student", student);
            navDrawerHashMap.put("Subject", subjects);
            navDrawerHashMap.put("Enrollment", enroll);
            navDrawerHashMap.put("Schedule", schedule);
            navDrawerHashMap.put("Grade", grade);
            navDrawerHashMap.put("Calendar",calendar);
            navDrawerHashMap.put("Messages", messages);
            navDrawerHashMap.put("Change Password", updatePassword);
            navDrawerHashMap.put("Log out", logout);

        }
        else if (userType.equals("instructor"))
        {
            List<MLAListModel> home = new ArrayList<>();

            List<MLAListModel> myInformation = new ArrayList<>();

            List<MLAListModel> student = new ArrayList<>();

            List<MLAListModel> schedule = new ArrayList<>();
            schedule.add(new MLAListModel("In-process "));
            schedule.add(new MLAListModel("Add Schedule"));
            schedule.add(new MLAListModel("Display Schedule"));
            schedule.add(new MLAListModel("Edit Schedule"));
            schedule.add(new MLAListModel("Remove Schedule"));

            List<MLAListModel> grade = new ArrayList<MLAListModel>();
            grade.add(new MLAListModel("Display Grade"));
            grade.add(new MLAListModel("Grade Graph"));

            List<MLAListModel> calendar = new ArrayList<MLAListModel>();

            List<MLAListModel> messages = new ArrayList<MLAListModel>();

            List<MLAListModel> updatePassword = new ArrayList<MLAListModel>();

            List<MLAListModel> logout = new ArrayList<MLAListModel>();

            navDrawerHashMap.put("Home", home);
            navDrawerHashMap.put("My Info", myInformation);
            navDrawerHashMap.put("Student", student);
            navDrawerHashMap.put("Schedule", schedule);
            navDrawerHashMap.put("Grade", grade);
            navDrawerHashMap.put("Calendar",calendar);
            navDrawerHashMap.put("Messages", messages);
            navDrawerHashMap.put("Change Password", updatePassword);
            navDrawerHashMap.put("Log out", logout);

        }
        else if(userType.equals("student"))
        {
            List<MLAListModel> home = new ArrayList<MLAListModel>();

            List<MLAListModel> myInformation = new ArrayList<MLAListModel>();

            List<MLAListModel> subjects = new ArrayList<MLAListModel>();

            List<MLAListModel> schedule = new ArrayList<MLAListModel>();
            schedule.add(new MLAListModel("In-process"));
            schedule.add(new MLAListModel("Display Schedule"));

            List<MLAListModel> grade = new ArrayList<MLAListModel>();

            List<MLAListModel> calendar = new ArrayList<MLAListModel>();

            List<MLAListModel> messages = new ArrayList<MLAListModel>();

            List<MLAListModel> updatePassword = new ArrayList<MLAListModel>();

            List<MLAListModel> logout = new ArrayList<MLAListModel>();

            navDrawerHashMap.put("Home", home);
            navDrawerHashMap.put("My Info", myInformation);
            navDrawerHashMap.put("Subject", subjects);
            navDrawerHashMap.put("Schedule", schedule);
            navDrawerHashMap.put("Grade", grade);
            navDrawerHashMap.put("Calendar",calendar);
            navDrawerHashMap.put("Messages", messages);
            navDrawerHashMap.put("Change Password", updatePassword);
            navDrawerHashMap.put("Log out", logout);

        }

        return navDrawerHashMap;
    }
}
