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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by paril on 7/22/2017.
 */
public class MLATaskViewFragment extends Fragment {
    List<MLASubjectDetails> subjectDetails = new ArrayList<MLASubjectDetails>();
    String[] description;

    String[] idSubject;
    PrefsManager prefsManager;

    Spinner spinnerSubject;

    List<MLATaskDetails> taskDetails = new ArrayList<MLATaskDetails>();
    ListView listViewTasks;
    MLATaskUpdateAdapter taskDisplayAdapter;


    View view;

    MLARegisterUsers register;

    // extract the extras that was sent from the previous intent
    void getExtra() {
        Intent previous = MLATaskViewFragment.this.getActivity().getIntent();
        Bundle bundle = previous.getExtras();
        if (bundle != null) {
            register.userId = (String) bundle.get("userId");
            register.userName = (String) bundle.get("userName");
            register.userType = (String) bundle.get("userType");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mla_tasksdisplay, container, false);
        spinnerSubject = (Spinner) view.findViewById(R.id.Mla_task_subjct_spinner);
        prefsManager = new PrefsManager(getActivity());
        listViewTasks = (ListView) view.findViewById(R.id.mla_task_display_listView);
        listViewTasks.setEmptyView(view.findViewById(R.id.empty_text_view));
        register = new MLARegisterUsers();

        // extract the extras that was sent from the previous intent
        getExtra();

        MLAGetAllSubjectAPI mlaGetAllSubjectAPI = new MLAGetAllSubjectAPI(this.getActivity());
        mlaGetAllSubjectAPI.execute();


        spinnerSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (register.userType.equals("instructor") || register.userType.equals("admin")) {
                    MLAGetTaskDetailsAPI mlaGetTaskDetailsAPI = new MLAGetTaskDetailsAPI(MLATaskViewFragment.this.getActivity());
                    mlaGetTaskDetailsAPI.execute();
                } else if (register.userType.equals("student")) {
                    MLAGetTaskDetailsStudenteAPI mlaGetTaskDetailsStudenteAPI = new MLAGetTaskDetailsStudenteAPI(MLATaskViewFragment.this.getActivity());
                    mlaGetTaskDetailsStudenteAPI.execute();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    //this class will get the task details and add them to listview item by item
    class MLAGetTaskDetailsStudenteAPI extends AsyncTask<Void, Void, List<MLATaskDetails>> {
        Context context;
        String subjectId = "";
        String stdId;

        public MLAGetTaskDetailsStudenteAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            subjectId = spinnerSubject.getSelectedItem().toString();
            stdId = register.userName;
        }

        //this method will be called after the doInBackground get all the admin users from the webapi
        @Override
        protected void onPostExecute(List<MLATaskDetails> taskDetails2) {
            //check if the call to api passed
            if (taskDetails2 != null) {
                taskDetails = taskDetails2;

                //fill the items in the listview with a customized adapter
                taskDisplayAdapter = new MLATaskUpdateAdapter(context,false, taskDetails);
                listViewTasks.setAdapter(taskDisplayAdapter);


            } else {

            }
        }

        @Override
        protected List<MLATaskDetails> doInBackground(Void... params) {
            try {
                Call<List<MLATaskDetails>> callTaskData = Api.getClient().getListTaskForStudent(subjectId, stdId);
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

    //this class will get the task details and add them to listview item by item
    class MLAGetTaskDetailsAPI extends AsyncTask<Void, Void, List<MLATaskDetails>> {
        Context context;
        String subjectId = "";

        public MLAGetTaskDetailsAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            subjectId = spinnerSubject.getSelectedItem().toString();
        }

        //this method will be called after the doInBackground get all the admin users from the webapi
        @Override
        protected void onPostExecute(List<MLATaskDetails> taskDetails2) {
            //check if the call to api passed
            if (taskDetails2 != null) {
                taskDetails = taskDetails2;

                //fill the items in the listview with a customized adapter
                listViewTasks = (ListView) view.findViewById(R.id.mla_task_display_listView);
                taskDisplayAdapter = new MLATaskUpdateAdapter(context,false ,taskDetails);
                listViewTasks.setAdapter(taskDisplayAdapter);

            } else {

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

    //this class will get the subject ids and add then to the spinner
    class MLAGetAllSubjectAPI extends AsyncTask<Void, Void, List<MLASubjectDetails>> {
        Context context;

        public MLAGetAllSubjectAPI(Context ctx) {
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

                    idSubject = new String[subjectDetails.size()];
                    for (int i = 0; i < subjectDetails.size(); i++) {
                        idSubject[i] = subjectDetails.get(i).idSubject;
                    }

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, idSubject);
                    spinnerSubject.setAdapter(arrayAdapter);
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

}
