package com.paril.mlaclientapp.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.android.internal.util.Predicate;
import com.paril.mlaclientapp.R;
import com.paril.mlaclientapp.model.MLARegisterUsers;
import com.paril.mlaclientapp.model.MLASubjectDetails;
import com.paril.mlaclientapp.model.MLATaskDetails;
import com.paril.mlaclientapp.ui.activity.MLAHomeActivity;
import com.paril.mlaclientapp.ui.adapter.MLATaskUpdateAdapter;
import com.paril.mlaclientapp.util.PrefsManager;
import com.paril.mlaclientapp.util.UserTypeData;
import com.paril.mlaclientapp.webservice.Api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by paril on 11/2/2017.
 */
public class MLATaskInProcessViewFragment extends Fragment {
    List<MLASubjectDetails> subjectDetails = new ArrayList<MLASubjectDetails>();
    String[] description;

    String[] strSubjectID;
    PrefsManager prefsManager;
    Spinner spnrSubject;

    List<MLATaskDetails> taskDetails = new ArrayList<MLATaskDetails>();
    ListView listViewInproccessTask;
    MLATaskUpdateAdapter taskDisplayAdapter;

    View view;

    MLARegisterUsers user;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mla_tasksdisplay,container,false);
        spnrSubject =(Spinner) view.findViewById(R.id.Mla_task_subjct_spinner);
        listViewInproccessTask = (ListView) view.findViewById(R.id.mla_task_display_listView);
        listViewInproccessTask.setEmptyView(view.findViewById(R.id.empty_text_view));
        user = new MLARegisterUsers();
        prefsManager=new PrefsManager(getActivity());

        getIntentService();

        MLAGetSubjectByStudentAPI getSubjectByStudentAPI=new MLAGetSubjectByStudentAPI(this.getActivity());
        getSubjectByStudentAPI.execute();

        spnrSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(user.userType.equals("instructor") || user.userType.equals("admin")) {
                    MLAGetTasksBySubjectAPI getTasksBySubjectAPI = new MLAGetTasksBySubjectAPI(MLATaskInProcessViewFragment.this.getActivity());
                    getTasksBySubjectAPI.execute();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    void getIntentService()
    {
        Intent previous= MLATaskInProcessViewFragment.this.getActivity().getIntent();
        Bundle bundle = previous.getExtras();
        if(bundle!=null)
        {
            user.userId =(String) bundle.get("userId");
            user.userName =(String) bundle.get("userName");
            user.userType =(String) bundle.get("userType");
        }
    }

    class MLAGetTasksBySubjectAPI extends AsyncTask<Void,Void,List<MLATaskDetails>>
    {
        Context context;
        String subjectId="";
        public MLAGetTasksBySubjectAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            subjectId = spnrSubject.getSelectedItem().toString();
        }

        @Override
        protected void onPostExecute(List<MLATaskDetails> taskDetails2) {

            if(taskDetails2 != null ) {
                taskDetails = taskDetails2;

                final ArrayList<MLATaskDetails> filterInProcess=new ArrayList<MLATaskDetails>();
                for (MLATaskDetails task:
                     taskDetails) {

                    if(isTaskInProcess(task.getSchedule_startTime(),task.getSchedule_endTime())){
                        filterInProcess.add(task);
                    }

                }
                taskDisplayAdapter = new MLATaskUpdateAdapter(context,prefsManager.getStringData("userType").equalsIgnoreCase(UserTypeData.INSTRUCTOR), filterInProcess);
                listViewInproccessTask.setAdapter(taskDisplayAdapter);
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
            ((MLAHomeActivity) getActivity()).showProgressDialog("Getting Subjects...");
        }

        @Override
        protected void onPostExecute(List<MLASubjectDetails> userDetails2) {
            ((MLAHomeActivity) getActivity()).hideProgressDialog();

            if (userDetails2 != null) {
                subjectDetails = new ArrayList<>();

                subjectDetails = filter(userDetails2, filterPredicate);
                if (subjectDetails.size() > 0) {

                    strSubjectID = new String[subjectDetails.size()];
                    for (int i = 0; i < subjectDetails.size(); i++) {
                        strSubjectID[i] = subjectDetails.get(i).idSubject;
                    }

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, strSubjectID);
                    spnrSubject.setAdapter(arrayAdapter);
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
            if (prefsManager.getStringData("userType").equals(UserTypeData.ADMIN)||prefsManager.getStringData("userType").equals(UserTypeData.STUDENT)) {
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


    private boolean isTaskInProcess(String startDateTime,String endDateTime){

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

            Date startDate = dateFormat.parse(startDateTime);

            Date endDate = dateFormat.parse(endDateTime);

            Date currentDate=new Date();

            if(currentDate.getTime()>startDate.getTime()&&currentDate.getTime()<endDate.getTime()){
                return true;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

}
