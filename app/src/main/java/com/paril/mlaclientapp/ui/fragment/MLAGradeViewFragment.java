
package com.paril.mlaclientapp.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.android.internal.util.Predicate;
import com.daimajia.swipe.util.Attributes;
import com.paril.mlaclientapp.R;
import com.paril.mlaclientapp.model.MLAGradeTask;
import com.paril.mlaclientapp.model.MLARegisterUsers;
import com.paril.mlaclientapp.model.MLASubjectDetails;
import com.paril.mlaclientapp.model.MLATaskDetails;
import com.paril.mlaclientapp.ui.activity.MLAHomeActivity;
import com.paril.mlaclientapp.ui.activity.MLAUpdateGradeActivity;
import com.paril.mlaclientapp.ui.adapter.MLAGradeAdapter;
import com.paril.mlaclientapp.ui.adapter.OnItemClickListener;
import com.paril.mlaclientapp.ui.view.EmptyRecyclerView;
import com.paril.mlaclientapp.util.PrefsManager;
import com.paril.mlaclientapp.util.UserTypeData;
import com.paril.mlaclientapp.util.VerticalSpaceItemDecoration;
import com.paril.mlaclientapp.webservice.Api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by paril on 7/23/2017.
 */
public class MLAGradeViewFragment extends Fragment {

    List<MLASubjectDetails> subjectDetails = new ArrayList<>();
    PrefsManager prefsManager;

    String[] subjectID;
    String[] subjectTopic;

    Spinner spnrSubjectTaskId;
    Spinner spnrSubject;

    List<MLATaskDetails> taskDetails = new ArrayList<>();
    List<MLAGradeTask> gradeDetails = new ArrayList<>();
    ListView listViewTasks;
    MLAGradeAdapter gradeDisplayAdapter;

    int spinnerTaskPosition = 0;

    View view;

    MLARegisterUsers user;
    EmptyRecyclerView recyclerViewUsers;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mla_gradedisplay, container, false);
        spnrSubject = (Spinner) view.findViewById(R.id.mla_gradefragment_spnersubject);
        spnrSubjectTaskId = (Spinner) view.findViewById(R.id.mla_gradefragment_spnertasks);
        user = new MLARegisterUsers();
        prefsManager = new PrefsManager(getActivity());
        recyclerViewUsers = (com.paril.mlaclientapp.ui.view.EmptyRecyclerView) view.findViewById(R.id.fragment_display_grade_recyclerView);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewUsers.addItemDecoration(new VerticalSpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.divider_list)));
        gradeDisplayAdapter = new MLAGradeAdapter(getActivity(), gradeDetails, new OnItemClickListener<MLAGradeTask>() {
            @Override
            public void onItemClick(MLAGradeTask item, int resourceId) {
                if (resourceId == R.id.row_display_grade_imgEditUser) {
                    final Intent intent = new Intent(MLAGradeViewFragment.this.getActivity(), MLAUpdateGradeActivity.class);
                    intent.putExtra("GradeData", item);
                    startActivity(intent);
                }
            }
        });

        gradeDisplayAdapter.setMode(Attributes.Mode.Single);
        recyclerViewUsers.setAdapter(gradeDisplayAdapter);
        recyclerViewUsers.setEmptyView(view.findViewById(R.id.fragment_display_grade_relEmptyView));

        getIntentService();

        // To add all the subjects within a subject spinner.
        MLAGetSubjectAPI getSubjectDetails = new MLAGetSubjectAPI(this.getActivity());
        getSubjectDetails.execute();

        spnrSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gradeDisplayAdapter = new MLAGradeAdapter(getActivity(), new ArrayList<MLAGradeTask>(), new OnItemClickListener<MLAGradeTask>() {
                    @Override
                    public void onItemClick(MLAGradeTask item, int resourceId) {
                        if (resourceId == R.id.row_display_grade_imgEditUser) {
                            final Intent intent = new Intent(MLAGradeViewFragment.this.getActivity(), MLAUpdateGradeActivity.class);
                            intent.putExtra("GradeData", item);
                            startActivity(intent);
                        }
                    }
                });

                gradeDisplayAdapter.setMode(Attributes.Mode.Single);
                recyclerViewUsers.setAdapter(gradeDisplayAdapter);
                recyclerViewUsers.setEmptyView(getView().findViewById(R.id.fragment_display_grade_relEmptyView));

                MLAGetTaskDetailsBySubjectAPI mlaGetTaskDetailsBySubjectAPI = new MLAGetTaskDetailsBySubjectAPI(MLAGradeViewFragment.this.getActivity());
                mlaGetTaskDetailsBySubjectAPI.execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnrSubjectTaskId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerTaskPosition = position;
                gradeDisplayAdapter = new MLAGradeAdapter(getActivity(), new ArrayList<MLAGradeTask>(), new OnItemClickListener<MLAGradeTask>() {
                    @Override
                    public void onItemClick(MLAGradeTask item, int resourceId) {
                        if (resourceId == R.id.row_display_grade_imgEditUser) {
                            final Intent intent = new Intent(MLAGradeViewFragment.this.getActivity(), MLAUpdateGradeActivity.class);
                            intent.putExtra("GradeData", item);
                            startActivity(intent);
                        }
                    }
                });

                gradeDisplayAdapter.setMode(Attributes.Mode.Single);
                recyclerViewUsers.setAdapter(gradeDisplayAdapter);
                recyclerViewUsers.setEmptyView(getView().findViewById(R.id.fragment_display_grade_relEmptyView));

                MLAGetStudentByTaskAPI mlaGetStudentByTaskAPI = new MLAGetStudentByTaskAPI(MLAGradeViewFragment.this.getActivity());
                mlaGetStudentByTaskAPI.execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (taskDetails != null && taskDetails.size() > 0) {
            MLAGetStudentByTaskAPI mlaGetStudentByTaskAPI = new MLAGetStudentByTaskAPI(MLAGradeViewFragment.this.getActivity());
            mlaGetStudentByTaskAPI.execute();
        }

    }

    void getIntentService() {
        Intent previous = MLAGradeViewFragment.this.getActivity().getIntent();
        Bundle bundle = previous.getExtras();
        if (bundle != null) {
            user.userId = (String) bundle.get("userId");
            user.userName = (String) bundle.get("userName");
            user.userType = (String) bundle.get("userType");
        }
    }

    class MLAGetStudentByTaskAPI extends AsyncTask<Void, Void, List<MLAGradeTask>> {
        Context context;
        String subjectId = "";
        String taskId = "";

        public MLAGetStudentByTaskAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            subjectId = spnrSubject.getSelectedItem().toString();
            taskId = taskDetails.get(spnrSubjectTaskId.getSelectedItemPosition()).idTask + "";
        }

        //this method will be called after the doInBackground get all the admin users from the webapi
        @Override
        protected void onPostExecute(List<MLAGradeTask> taskDetails) {
            //check if the call to api passed
            gradeDetails = new ArrayList<MLAGradeTask>();
            if (taskDetails != null) {
                gradeDetails = taskDetails;
            } else {
                ((MLAHomeActivity) getActivity()).showSnackBar(getString(R.string.server_error), getView().findViewById(R.id.fragment_display_grade_coordinatorLayout));
            }
            gradeDisplayAdapter = new MLAGradeAdapter(context, gradeDetails, new OnItemClickListener<MLAGradeTask>() {
                @Override
                public void onItemClick(MLAGradeTask item, int resourceId) {
                    if (resourceId == R.id.row_display_grade_imgEditUser) {
                        final Intent intent = new Intent(MLAGradeViewFragment.this.getActivity(), MLAUpdateGradeActivity.class);
                        intent.putExtra("GradeData", item);
                        startActivity(intent);
                    }
                }
            });

            gradeDisplayAdapter.setMode(Attributes.Mode.Single);
            recyclerViewUsers.setAdapter(gradeDisplayAdapter);
            recyclerViewUsers.setEmptyView(getView().findViewById(R.id.fragment_display_grade_relEmptyView));
        }

        @Override
        protected List<MLAGradeTask> doInBackground(Void... params) {

            try {
                Call<List<MLAGradeTask
                        >> callTaskData = Api.getClient().getGrades(taskId, subjectId);
                Response<List<MLAGradeTask>> responseTaskData = callTaskData.execute();
                if (responseTaskData.isSuccessful() && responseTaskData.body() != null) {
                    return responseTaskData.body();
                } else {
                    return null;
                }

            } catch (MalformedURLException e) {
                return null;


            } catch (IOException e) {
                return null;
            }

        }
    }

    class MLAGetTaskDetailsBySubjectAPI extends AsyncTask<Void, Void, List<MLATaskDetails>> {
        Context context;
        String subjectId = "";

        public MLAGetTaskDetailsBySubjectAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            subjectId = spnrSubject.getSelectedItem().toString();
        }

        //this method will be called after the doInBackground get all the admin users from the webapi
        @Override
        protected void onPostExecute(List<MLATaskDetails> taskDetails2) {
            //check if the call to api passed
            if (taskDetails2 != null) {
                taskDetails = new ArrayList<MLATaskDetails>();
                for (int i = 0; i < taskDetails2.size(); i++) {
                    if (isTaskFinished(taskDetails2.get(i).getSchedule_endTime())) {
                        taskDetails.add(taskDetails2.get(i));
                    }
                }
                subjectTopic = new String[taskDetails.size()];
                for (int i = 0; i < taskDetails.size(); i++) {
                    if (!TextUtils.isEmpty(taskDetails.get(i).getTopic())) {
                        subjectTopic[i] = taskDetails.get(i).getTopic();
                    } else {
                        subjectTopic[i] = getDateString(taskDetails.get(i).getSchedule_startTime(), taskDetails.get(i).getSchedule_endTime());
                    }
                }

                spnrSubjectTaskId = (Spinner) view.findViewById(R.id.mla_gradefragment_spnertasks);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, subjectTopic);
                spnrSubjectTaskId.setAdapter(arrayAdapter);
            }
        }

        @Override
        protected List<MLATaskDetails> doInBackground(Void... params) {
            try {
                Call<List<MLATaskDetails>> callTaskData = Api.getClient().getTasksBySubject(subjectId);
                Response<List<MLATaskDetails>> responseTaskData = callTaskData.execute();
                if (responseTaskData.isSuccessful() && responseTaskData.body() != null) {
                    return responseTaskData.body();
                } else {
                    return null;
                }

            } catch (MalformedURLException e) {
                return null;

            } catch (IOException e) {
                return null;
            }


        }
    }

    class MLAGetSubjectAPI extends AsyncTask<Void, Void, List<MLASubjectDetails>> {
        Context context;

        public MLAGetSubjectAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            ((MLAHomeActivity) getActivity()).showProgressDialog("Getting Subjects...");
        }

        @Override
        protected void onPostExecute(List<MLASubjectDetails> userDetails2) {
            ((MLAHomeActivity) getActivity()).hideProgressDialog();

            if (userDetails2 != null) {
                subjectDetails = new ArrayList<>();

                subjectDetails = filter(userDetails2, filterPredicate);
                if (subjectDetails.size() > 0) {

                    subjectID = new String[subjectDetails.size()];
                    for (int i = 0; i < subjectDetails.size(); i++) {
                        subjectID[i] = subjectDetails.get(i).idSubject;
                    }
                    //add the subject ids to the spinner
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, subjectID);
                    spnrSubject.setAdapter(arrayAdapter);
                } else {

                }
            }
        }

        @Override
        protected List<MLASubjectDetails> doInBackground(Void... params) {
            try {
                if (prefsManager.getStringData("userType").equalsIgnoreCase(UserTypeData.STUDENT)) {
                    Call<ArrayList<MLASubjectDetails>> callSubjectData = Api.getClient().getSubjForStudent(prefsManager.getStringData("userName"));
                    Response<ArrayList<MLASubjectDetails>> responseSubjectData = callSubjectData.execute();
                    if (responseSubjectData.isSuccessful() && responseSubjectData.body() != null) {
                        return responseSubjectData.body();
                    } else {
                        return null;
                    }
                } else {
                    Call<List<MLASubjectDetails>> callSubjectData = Api.getClient().getAllSubject();
                    Response<List<MLASubjectDetails>> responseSubjectData = callSubjectData.execute();
                    if (responseSubjectData.isSuccessful() && responseSubjectData.body() != null) {
                        return responseSubjectData.body();
                    } else {
                        return null;
                    }
                }


            } catch (MalformedURLException e) {
                return null;

            } catch (IOException e) {
                return null;
            }
        }
    }

    Predicate<MLASubjectDetails> filterPredicate = new Predicate<MLASubjectDetails>() {
        public boolean apply(MLASubjectDetails obj) {
            if (prefsManager.getStringData("userType").equals(UserTypeData.ADMIN) || prefsManager.getStringData("userType").equals(UserTypeData.STUDENT)) {
                return true;
            } else if (prefsManager.getStringData("userType").equals(UserTypeData.INSTRUCTOR) && obj.getIdInstructor().equals(prefsManager.getStringData("userName"))) {
                return true;
            }
            return false;
        }

    };

    public static <T> ArrayList<T> filter(Collection<T> source, Predicate<T> predicate) {
        ArrayList<T> result = new ArrayList<T>();
        for (T element : source) {
            if (predicate.apply(element)) {
                result.add(element);
            }
        }
        return result;
    }

    private String getDateString(String startDateString, String endDateString) {
        Calendar calendar = null;
        String durationString = "";
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

            Date date = dateFormat.parse(startDateString);

            calendar = Calendar.getInstance();
            calendar.setTime(date);
            String dateString = calendar.get(Calendar.DATE) > 9 ? calendar.get(Calendar.DATE) + "" : "0" + calendar.get(Calendar.DATE);
            String monthString = (calendar.get(Calendar.MONTH) + 1) > 9 ? ((calendar.get(Calendar.MONTH) + 1) + "") : ("0" + (calendar.get(Calendar.MONTH) + 1)
            );
            String hourString = calendar.get(Calendar.HOUR_OF_DAY) > 9 ? calendar.get(Calendar.HOUR_OF_DAY) + "" : ("0" + calendar.get(Calendar.HOUR_OF_DAY));
            String minuteString = calendar.get(Calendar.MINUTE) > 9 ? calendar.get(Calendar.MINUTE) + "" : ("0" + calendar.get(Calendar.MINUTE));
            Date endDate = dateFormat.parse(endDateString);
            calendar.setTime(endDate);
            String endHourString = calendar.get(Calendar.HOUR_OF_DAY) > 9 ? calendar.get(Calendar.HOUR_OF_DAY) + "" : ("0" + calendar.get(Calendar.HOUR_OF_DAY));
            String endMinuteString = calendar.get(Calendar.MINUTE) > 9 ? calendar.get(Calendar.MINUTE) + "" : ("0" + calendar.get(Calendar.MINUTE));

            durationString = monthString + "/" + dateString + "/" + calendar.get(Calendar.YEAR) + "," + hourString + ":" + minuteString + "-" + endHourString + ":" + endMinuteString;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return durationString;

    }

    private boolean isTaskFinished(String endDateTime) {

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date endDate = dateFormat.parse(endDateTime);
            Date currentDate = new Date();
            if (currentDate.getTime() > endDate.getTime()) {
                return true;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;

    }
}
