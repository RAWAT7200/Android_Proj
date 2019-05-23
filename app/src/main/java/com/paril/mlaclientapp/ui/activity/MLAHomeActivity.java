package com.paril.mlaclientapp.ui.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.paril.mlaclientapp.R;
import com.paril.mlaclientapp.model.MLARegisterUsers;
import com.paril.mlaclientapp.model.MLAListModel;
import com.paril.mlaclientapp.model.MLAListData;
import com.paril.mlaclientapp.service.AlertTaskIntentService;
import com.paril.mlaclientapp.ui.adapter.MLAMenuAdapter;
import com.paril.mlaclientapp.ui.fragment.ChatMessageActivity;
import com.paril.mlaclientapp.ui.fragment.CreateMessage;
import com.paril.mlaclientapp.ui.fragment.MLAGradeGraphFragment;
import com.paril.mlaclientapp.ui.fragment.MLAAdminViewFragment;
import com.paril.mlaclientapp.ui.fragment.MLAAssignedSubjectViewFragment;
import com.paril.mlaclientapp.ui.fragment.MLAEditPasswordFragment;
import com.paril.mlaclientapp.ui.fragment.MLAEditTaskFragment;
import com.paril.mlaclientapp.ui.fragment.MLAEnrollStudentFragment;
import com.paril.mlaclientapp.ui.fragment.MLAEnrollStudentViewFragment;
import com.paril.mlaclientapp.ui.fragment.MLAGradeStudentViewFragment;
import com.paril.mlaclientapp.ui.fragment.MLAGradeViewFragment;
import com.paril.mlaclientapp.ui.fragment.MLAHomeFragment;
import com.paril.mlaclientapp.ui.fragment.MLAInProcessTasksFragment;
import com.paril.mlaclientapp.ui.fragment.MLAInstructStudentViewFragment;
import com.paril.mlaclientapp.ui.fragment.MLAInstructorViewFragment;
import com.paril.mlaclientapp.ui.fragment.MLAMessageViewFragment;
import com.paril.mlaclientapp.ui.fragment.MLAMyInformationFragment;
import com.paril.mlaclientapp.ui.fragment.MLANewMessageFragment;
import com.paril.mlaclientapp.ui.fragment.MLAScheduleAddFragment;
import com.paril.mlaclientapp.ui.fragment.MLAStudentDisEnrollFragment;
import com.paril.mlaclientapp.ui.fragment.MLAStudentSubjectViewFragment;
import com.paril.mlaclientapp.ui.fragment.MLAStudentViewFragment;
import com.paril.mlaclientapp.ui.fragment.MLASubjectViewFragment;
import com.paril.mlaclientapp.ui.fragment.MLATaskInProcessViewFragment;
import com.paril.mlaclientapp.ui.fragment.MLATaskViewFragment;
import com.paril.mlaclientapp.ui.fragment.MLATasksDeleteFragment;
import com.paril.mlaclientapp.util.PrefsManager;
import com.paril.mlaclientapp.util.UserTypeData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by paril on 7/19/2017.
 */
public class MLAHomeActivity extends BaseActivity {

    ActionBarDrawerToggle actionBarDrawerToggle;
    ActionBar actionBar;

    HashMap<String, List<MLAListModel>> listHashMap;
    MLAListData mlaListData;
    DrawerLayout drawerLayout;
    List<String> listPrimary;

    MLAMenuAdapter adapter;

    private int lastGroupSelPos = -1, lastChildSelPos = -1;
    ExpandableListView listExpandable;
    LinearLayout navDrawerList;

    MLARegisterUsers user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mla_home);
        user = new MLARegisterUsers();
        intentService();
        configureData();
        configureNavDrawer();
        startService(new Intent(this, AlertTaskIntentService.class));
        if (savedInstanceState == null) {
            onHomeClick();
        }
    }

    public void openMessage()
    {
        setToolbarTitle("INBOX");

        ChatMessageActivity infoFragment = new ChatMessageActivity();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mla_fragmentholder, infoFragment);
        fragmentTransaction.commit();

    }
    public void createMessage() {


        setToolbarTitle("CHAT MESSAGE");

        CreateMessage infoFragment = new CreateMessage();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mla_fragmentholder, infoFragment);
        fragmentTransaction.commit();

    }


    public void onHomeClick() {
        setToolbarTitle("Home");
        UserTypeData userTypeData = new UserTypeData(user.userType);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (userTypeData.getUserType()) {

            case UserTypeData.ADMIN:
                MLAHomeFragment displayTask = new MLAHomeFragment();
                fragmentTransaction.replace(R.id.mla_fragmentholder, displayTask);
                fragmentTransaction.commit();
                break;

            case UserTypeData.INSTRUCTOR:
                MLAAssignedSubjectViewFragment taskFragment = new MLAAssignedSubjectViewFragment();
                fragmentTransaction.replace(R.id.mla_fragmentholder, taskFragment);
                fragmentTransaction.commit();
                break;

            case UserTypeData.STUDENT:
                MLAStudentSubjectViewFragment MLAInProgressTasksFragment = new MLAStudentSubjectViewFragment();
                fragmentTransaction.replace(R.id.mla_fragmentholder, MLAInProgressTasksFragment);
                fragmentTransaction.commit();
                break;
            case UserTypeData.MESSAGE:
                ChatMessageActivity displayTasks = new ChatMessageActivity();
                fragmentTransaction.replace(R.id.mla_fragmentholder, displayTasks);
                fragmentTransaction.commit();
                break;

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    public void openCourse() {

        setToolbarTitle("Subject");
        lastGroupSelPos = 5;
        MLASubjectViewFragment infoFragment = new MLASubjectViewFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mla_fragmentholder, infoFragment);
        fragmentTransaction.commit();
    }


    public void openInstruction() {
        lastGroupSelPos = 3;
        setToolbarTitle("Instructor");
        MLAInstructorViewFragment infoFragment = new MLAInstructorViewFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mla_fragmentholder, infoFragment);
        fragmentTransaction.commit();
    }

    public void setToolbarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    void intentService() {
        Intent previous = getIntent();
        Bundle bundle = previous.getExtras();
        if (bundle != null) {
            user.userId = (String) bundle.get("userId");
            user.userName = (String) bundle.get("userName");
            user.userType = (String) bundle.get("userType");
        }
    }

    public void openStudent() {
        lastGroupSelPos = 4;
        setToolbarTitle("Student");
        if (user.userType.equalsIgnoreCase(UserTypeData.INSTRUCTOR)) {
            MLAInstructStudentViewFragment infoFragment = new MLAInstructStudentViewFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.mla_fragmentholder, infoFragment);
            fragmentTransaction.commit();
        } else {
            MLAStudentViewFragment infoFragment = new MLAStudentViewFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.mla_fragmentholder, infoFragment);
            fragmentTransaction.commit();
        }
    }


    public void parentClicking(){
        listExpandable.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {
                if (expandableListView.getExpandableListAdapter().getChildrenCount(groupPosition) == 0) {
                    if (lastGroupSelPos != groupPosition) {
                        if (listPrimary.get(groupPosition).equals("Home")) {
                            onHomeClick();

                        } else if (listPrimary.get(groupPosition).equals("My Info")) {
                            setToolbarTitle("My Info");
                            MLAMyInformationFragment infoFragment = new MLAMyInformationFragment();
                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.mla_fragmentholder, infoFragment);
                            fragmentTransaction.commit();

                        } else if (listPrimary.get(groupPosition).equals("Change Password")) {
                            setToolbarTitle("Change Password");
                            MLAEditPasswordFragment MLAEditPasswordFragment = new MLAEditPasswordFragment();
                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.mla_fragmentholder, MLAEditPasswordFragment);
                            fragmentTransaction.commit();

                        } else if (listPrimary.get(groupPosition).equals("Log out")) {
                            setToolbarTitle("Log out");
                            PrefsManager prefsManager = new PrefsManager(MLAHomeActivity.this);
                            prefsManager.clearData();
                            final Intent intent = new Intent(MLAHomeActivity.this, AlertTaskIntentService.class);
                            intent.putExtra("stop", true);
                            startService(intent);
                            if (getSinchServiceInterface().isStarted() && getSinchServiceInterface().isBinderAlive()) {
                                getSinchServiceInterface().stopClient();
                            }
                            LocalBroadcastManager.getInstance(MLAHomeActivity.this).sendBroadcast(new Intent(BaseActivity.ACTION_FINISH));
                            startActivity(new Intent(MLAHomeActivity.this, MLALoginActivity.class));

                        } else if (listPrimary.get(groupPosition).equals("Calendar")) {
                            //This intent is created to create the google calendar
                            Intent intent = new Intent();
                            intent.setComponent(new ComponentName("com.google.android.calendar", "com.android.calendar.LaunchActivity"));
                            PackageManager manager = getPackageManager();
                            List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
                            if (infos.size() > 0) {
                                startActivity(intent);
                            } else {
                                showSnackBar("Please install Google Calendar Application.", findViewById(R.id.activity_mla_coordinatorLayout));
                            }
                        } else if (listPrimary.get(groupPosition).equals("Admin")) {
                            setToolbarTitle("Admin");
                            MLAAdminViewFragment infoFragment = new MLAAdminViewFragment();
                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.mla_fragmentholder, infoFragment);
                            fragmentTransaction.commit();
                        } else if (listPrimary.get(groupPosition).equals("Grade")) {
                            setToolbarTitle("Grade");
                            if (user.userType.equals("admin") ) {
                                MLAGradeViewFragment MLAGradeViewFragment = new MLAGradeViewFragment();
                                FragmentManager fragmentManager = getFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.mla_fragmentholder, MLAGradeViewFragment);
                                fragmentTransaction.commit();
                            } else if (user.userType.equals("student")) {
                                MLAGradeStudentViewFragment MLAGradeStudentViewFragment = new MLAGradeStudentViewFragment();
                                FragmentManager fragmentManager = getFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.mla_fragmentholder, MLAGradeStudentViewFragment);
                                fragmentTransaction.commit();
                            }
                        } else if (listPrimary.get(groupPosition).equals("Student")) {
                            setToolbarTitle("Student");

                            if (user.userType.equalsIgnoreCase(UserTypeData.INSTRUCTOR)) {
                                MLAInstructStudentViewFragment infoFragment = new MLAInstructStudentViewFragment();
                                FragmentManager fragmentManager = getFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.mla_fragmentholder, infoFragment);
                                fragmentTransaction.commit();
                            } else {
                                MLAStudentViewFragment infoFragment = new MLAStudentViewFragment();
                                FragmentManager fragmentManager = getFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.mla_fragmentholder, infoFragment);
                                fragmentTransaction.commit();
                            }
                        } else if (listPrimary.get(groupPosition).equals("Instructor")) {
                            setToolbarTitle("Instructor");
                            MLAInstructorViewFragment infoFragment = new MLAInstructorViewFragment();
                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.mla_fragmentholder, infoFragment);
                            fragmentTransaction.commit();
                        } else if (listPrimary.get(groupPosition).equals("Subject")) {
                            setToolbarTitle("Subject");
                            MLASubjectViewFragment infoFragment = new MLASubjectViewFragment();
                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.mla_fragmentholder, infoFragment);
                            fragmentTransaction.commit();
                        }  else if (listPrimary.get(groupPosition).equals("Messages")) {
                            setToolbarTitle("New Message");
                            MLANewMessageFragment mlaNewMessageFragment = new MLANewMessageFragment();
                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.mla_fragmentholder, mlaNewMessageFragment);
                            fragmentTransaction.commit();
                        }else {
                            Toast.makeText(MLAHomeActivity.this, "Screen is still under development.", Toast.LENGTH_LONG).show();
                        }
                    }
                    drawerLayout.closeDrawer(navDrawerList);
                    lastGroupSelPos = groupPosition;
                }
                return false;
            }


        });

    }


    public void childClicking(){

        listExpandable.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if (lastGroupSelPos != groupPosition || lastChildSelPos != childPosition) {

                    if (listHashMap.get(listPrimary.get(groupPosition)).get(childPosition).getTxtView().equals("Display") && listPrimary.get(groupPosition).equals("Enrollment")) {
                        setToolbarTitle("Enroll Student Display");
                        MLAEnrollStudentViewFragment MLAEnrollStudentViewFragment = new MLAEnrollStudentViewFragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.mla_fragmentholder, MLAEnrollStudentViewFragment);
                        fragmentTransaction.commit();
                    } else if (listHashMap.get(listPrimary.get(groupPosition)).get(childPosition).getTxtView().equals("Drop") && listPrimary.get(groupPosition).equals("Enrollment")) {
                        setToolbarTitle("Disenroll Student");
                        MLAStudentDisEnrollFragment MLAStudentDisEnrollFragment = new MLAStudentDisEnrollFragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.mla_fragmentholder, MLAStudentDisEnrollFragment);
                        fragmentTransaction.commit();
                    } else if (listHashMap.get(listPrimary.get(groupPosition)).get(childPosition).getTxtView().equals("Enroll") && listPrimary.get(groupPosition).equals("Enrollment")) {
                        setToolbarTitle("Enroll Student");
                        MLAEnrollStudentFragment enrollFragment = new MLAEnrollStudentFragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.mla_fragmentholder, enrollFragment);
                        fragmentTransaction.commit();
                    } else if (listHashMap.get(listPrimary.get(groupPosition)).get(childPosition).getTxtView().equals("Add Schedule") && listPrimary.get(groupPosition).equals("Schedule")) {
                        setToolbarTitle("Add Schedule");
                        MLAScheduleAddFragment MLAScheduleAddFragment = new MLAScheduleAddFragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.mla_fragmentholder, MLAScheduleAddFragment);
                        fragmentTransaction.commit();
                    } else if (listHashMap.get(listPrimary.get(groupPosition)).get(childPosition).getTxtView().equals("Display Schedule") && listPrimary.get(groupPosition).equals("Schedule")) {
                        setToolbarTitle("Display Schedule");

                        MLATaskViewFragment displayTask = new MLATaskViewFragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.mla_fragmentholder, displayTask);
                        fragmentTransaction.commit();
                    } else if (listHashMap.get(listPrimary.get(groupPosition)).get(childPosition).getTxtView().equals("Edit Schedule") && listPrimary.get(groupPosition).equals("Schedule")) {
                        setToolbarTitle("Edit Schedule");

                        MLAEditTaskFragment MLAEditTaskFragment = new MLAEditTaskFragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.mla_fragmentholder, MLAEditTaskFragment);
                        fragmentTransaction.commit();
                    } else if (listHashMap.get(listPrimary.get(groupPosition)).get(childPosition).getTxtView().equals("In-process") && listPrimary.get(groupPosition).equals("Schedule")) {
                        setToolbarTitle("In-process");

                        MLAInProcessTasksFragment inProcessTasksFragment = new MLAInProcessTasksFragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.mla_fragmentholder, inProcessTasksFragment);
                        fragmentTransaction.commit();
                    } else if (listHashMap.get(listPrimary.get(groupPosition)).get(childPosition).getTxtView().equals("In-process ") && listPrimary.get(groupPosition).equals("Schedule")) {
                        setToolbarTitle("In-process");

                        MLATaskInProcessViewFragment dsplayTaskInProcess = new MLATaskInProcessViewFragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.mla_fragmentholder, dsplayTaskInProcess);
                        fragmentTransaction.commit();
                    } else if (listHashMap.get(listPrimary.get(groupPosition)).get(childPosition).getTxtView().equals("Remove Schedule") && listPrimary.get(groupPosition).equals("Schedule")) {
                        setToolbarTitle("Remove Schedule");

                        MLATasksDeleteFragment MLATasksDeleteFragment = new MLATasksDeleteFragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.mla_fragmentholder, MLATasksDeleteFragment);
                        fragmentTransaction.commit();
                    } else if (listHashMap.get(listPrimary.get(groupPosition)).get(childPosition).getTxtView().equals("New") && listPrimary.get(groupPosition).equals("Messages")) {
                        setToolbarTitle("New Message");

                        MLANewMessageFragment MLAMLANewMessageFragment = new MLANewMessageFragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.mla_fragmentholder, MLAMLANewMessageFragment);
                        fragmentTransaction.commit();
                    } else if (listHashMap.get(listPrimary.get(groupPosition)).get(childPosition).getTxtView().equals("Display") && listPrimary.get(groupPosition).equals("Messages")) {
                        setToolbarTitle("Display Message");

                        MLAMessageViewFragment displayGmail = new MLAMessageViewFragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.mla_fragmentholder, displayGmail);
                        fragmentTransaction.commit();
                    } else if (listHashMap.get(listPrimary.get(groupPosition)).get(childPosition).getTxtView().equals("Display Grade") && listPrimary.get(groupPosition).equals("Grade")) {
                        setToolbarTitle("Display Grade");

                        MLAGradeViewFragment MLAGradeViewFragment = new MLAGradeViewFragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.mla_fragmentholder, MLAGradeViewFragment);
                        fragmentTransaction.commit();
                    } else if (listHashMap.get(listPrimary.get(groupPosition)).get(childPosition).getTxtView().equals("Grade Graph") && listPrimary.get(groupPosition).equals("Grade")) {
                        setToolbarTitle("Grade Graph");

                        MLAGradeGraphFragment gradeGraphFragment = new MLAGradeGraphFragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.mla_fragmentholder, gradeGraphFragment);
                        fragmentTransaction.commit();

                    } else {
                        Toast.makeText(MLAHomeActivity.this, "Screen is still under development.", Toast.LENGTH_LONG).show();
                    }
                }
                lastGroupSelPos = groupPosition;
                lastChildSelPos = childPosition;
                drawerLayout.closeDrawer(navDrawerList);
                return true;
            }
        });
    }

    public void configureNavDrawer() {
        parentClicking();
        childClicking();
        listExpandable.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
            }
        });

        listExpandable.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
            }
        });
    }


    public void configureData(){
        // Configure list view with data.
        listExpandable = (ExpandableListView) findViewById(R.id.expandableList);
        mlaListData = new MLAListData(user.userType);
        listHashMap = mlaListData.getlist();
        listPrimary = new ArrayList<>(listHashMap.keySet());

        // Configure the data in Adapter.
        adapter = new MLAMenuAdapter(this, listHashMap, listPrimary);
        listExpandable.setAdapter(adapter);

        drawerLayout = (DrawerLayout) findViewById(R.id.dawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.mla_drawer_open, R.string.mla_drawer_close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        navDrawerList = (LinearLayout) findViewById(R.id.drawer1);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(navDrawerList)) {
                drawerLayout.closeDrawer(navDrawerList);
            } else {
                drawerLayout.openDrawer(navDrawerList);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();

    }
}
