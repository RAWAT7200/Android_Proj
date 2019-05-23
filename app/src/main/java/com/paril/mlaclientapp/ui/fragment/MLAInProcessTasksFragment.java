package com.paril.mlaclientapp.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.paril.mlaclientapp.R;
import com.paril.mlaclientapp.model.MLARegisterUsers;
import com.paril.mlaclientapp.model.MLASubjectDetails;
import com.paril.mlaclientapp.model.MLATaskDetails;
import com.paril.mlaclientapp.ui.adapter.MLATaskInProgressAdapter;
import com.paril.mlaclientapp.util.PrefsManager;
import com.paril.mlaclientapp.webservice.Api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by paril on 7/27/2017.
 */
public class MLAInProcessTasksFragment extends Fragment {
    List<MLASubjectDetails> subjectDetails = new ArrayList<MLASubjectDetails>();

    String[] strSubjectId;
    String[] strSubjectTitle;
    String[] strSubjectDescription;
    String[] description;

    List<MLATaskDetails> taskDetails = new ArrayList<MLATaskDetails>();
    ListView listViewTasks;
    Spinner spnrSubject;

    MLATaskInProgressAdapter taskDisplayAdapter;
    PrefsManager prefsManager;

    Handler handler = new Handler();
    Timer timer;
    View view;

    MLARegisterUsers user;

    @Override
    public void onPause() {
        super.onPause();
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (taskDisplayAdapter != null && taskDisplayAdapter.getCount() > 0) {
            startTimer();

        }
    }

    private void startTimer() {
        if (timer != null) {
            timer.cancel();

        }

        timer = new Timer("UpdateProgress");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (taskDisplayAdapter != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            taskDisplayAdapter.updateTaskProgress();
                        }
                    });
                }

            }
        }, 000, 3000);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mla_tasksinprocess, container, false);
        spnrSubject = (Spinner) view.findViewById(R.id.mla_inprocesstask_spnrSubject);
        listViewTasks = (ListView) view.findViewById(R.id.mla_inprocesstask_listview);
        listViewTasks.setEmptyView(view.findViewById(R.id.empty_text_view));
        user = new MLARegisterUsers();

        getIntentService();
        prefsManager = new PrefsManager(getActivity());

        MLAGetSubjectByStudentAPI getSubjectByStudentAPI = new MLAGetSubjectByStudentAPI(this.getActivity());
        getSubjectByStudentAPI.execute();


        spnrSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                MLAGetTasksBySubjectAPI getTasksBySubjectAPI = new MLAGetTasksBySubjectAPI(MLAInProcessTasksFragment.this.getActivity());
                getTasksBySubjectAPI.execute();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return view;
    }

    void getIntentService() {
        Intent previous = MLAInProcessTasksFragment.this.getActivity().getIntent();
        Bundle bundle = previous.getExtras();
        if (bundle != null) {
            user.userId = (String) bundle.get("userId");
            user.userName = (String) bundle.get("userName");
            user.userType = (String) bundle.get("userType");
        }
    }


    class MLAGetTasksBySubjectAPI extends AsyncTask<Void, Void, List<MLATaskDetails>> {
        Context context;
        String subjectId = "";

        public MLAGetTasksBySubjectAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            subjectId = spnrSubject.getSelectedItem().toString();
        }

        @Override
        protected void onPostExecute(List<MLATaskDetails> taskDetails2) {
            final ArrayList<MLATaskDetails> filterInProcess = new ArrayList<MLATaskDetails>();

            if (taskDetails2 != null && taskDetails2.size() > 0) {
                taskDetails = taskDetails2;


                for (MLATaskDetails task :
                        taskDetails) {

                    if (isTaskInProcess(task.getSchedule_startTime(), task.getSchedule_endTime())) {
                        filterInProcess.add(task);
                    }

                }
                if (filterInProcess != null && filterInProcess.size() > 0) {
                    taskDisplayAdapter = new MLATaskInProgressAdapter(context, filterInProcess);
                    listViewTasks.setAdapter(taskDisplayAdapter);
                    startTimer();

                } else {
                    if (timer != null) {
                        timer.cancel();

                    }
                    taskDisplayAdapter = new MLATaskInProgressAdapter(context, filterInProcess);
                    listViewTasks.setAdapter(taskDisplayAdapter);
                }
            } else {
                if (timer != null) {
                    timer.cancel();

                }
                taskDisplayAdapter = new MLATaskInProgressAdapter(context, filterInProcess);
                listViewTasks.setAdapter(taskDisplayAdapter);

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

    class MLAGetSubjectByStudentAPI extends AsyncTask<Void, Void, List<MLASubjectDetails>> {
        Context context;

        public MLAGetSubjectByStudentAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(List<MLASubjectDetails> userDetails2) {
            if (userDetails2 != null) {
                subjectDetails = userDetails2;
                strSubjectId = new String[subjectDetails.size()];
                strSubjectTitle = new String[subjectDetails.size()];
                strSubjectDescription = new String[subjectDetails.size()];
                for (int i = 0; i < subjectDetails.size(); i++) {
                    strSubjectId[i] = subjectDetails.get(i).idSubject;
                    strSubjectTitle[i] = subjectDetails.get(i).title;
                    strSubjectTitle[i] += "    (" + subjectDetails.get(i).idSubject + ")";

                    strSubjectDescription[i] = subjectDetails.get(i).description;
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, strSubjectId);
                spnrSubject.setAdapter(arrayAdapter);
            }
        }

        @Override
        protected List<MLASubjectDetails> doInBackground(Void... params) {
            try {
                Call<ArrayList<MLASubjectDetails>> callSubjectData = Api.getClient().getSubjForStudent(prefsManager.getStringData("userName"));
                Response<ArrayList<MLASubjectDetails>> responseSubjectData = callSubjectData.execute();
                if (responseSubjectData.isSuccessful() && responseSubjectData.body() != null) {
                    return responseSubjectData.body();
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

    private boolean isTaskInProcess(String startDateTime, String endDateTime) {

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

            Date startDate = dateFormat.parse(startDateTime);

            Date endDate = dateFormat.parse(endDateTime);

            Date currentDate = new Date();

            if (currentDate.getTime() > startDate.getTime() && currentDate.getTime() < endDate.getTime()) {
                return true;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;

    }


}
