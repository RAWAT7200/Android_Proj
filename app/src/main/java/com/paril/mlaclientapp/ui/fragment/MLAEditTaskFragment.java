package com.paril.mlaclientapp.ui.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
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
public class MLAEditTaskFragment extends Fragment {
    List<MLASubjectDetails> subjectDetails = new ArrayList<MLASubjectDetails>();

    String[] strSubjectId;
    Spinner spinnerSubject;

    List<MLATaskDetails> taskDetails = new ArrayList<MLATaskDetails>();
    ListView listViewTasks;
    MLATaskUpdateAdapter taskUpdateAdapter;
    PrefsManager prefsManager;

    View view;

    private String subIdSel = "";


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        subIdSel="";
        if (args != null && args.containsKey("subId")) {
            subIdSel = args.getString("subId");
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mla_taskupdate, container, false);
        spinnerSubject = (Spinner) view.findViewById(R.id.mla_taskupdate_spnrSubject);
        listViewTasks = (ListView) view.findViewById(R.id.mla_taskupdate_listView);
        listViewTasks.setEmptyView(view.findViewById(R.id.fragment_updated_task_relEmptyView));
        prefsManager = new PrefsManager(getActivity());

        getIntentService();

        MLAGetAllSubjectAPI getAllSubjectAPI = new MLAGetAllSubjectAPI(this.getActivity());
        getAllSubjectAPI.execute();


        spinnerSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MLAGetTasksBySubjectAPI getTasksBySubjectAPI = new MLAGetTasksBySubjectAPI(MLAEditTaskFragment.this.getActivity());
                getTasksBySubjectAPI.execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        listViewTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentManager faFragmentManager = getFragmentManager();
                MLAEditTaskDialog updateTaskDialog = MLAEditTaskDialog.newInstance(MLAEditTaskFragment.this, taskDetails.get(position).idTask + "", taskDetails.get(position).topic, taskDetails.get(position).description);

                updateTaskDialog.show(faFragmentManager, "updateTaskDialog");
            }
        });

        return view;
    }

    public void refresh() {
        MLAGetTasksBySubjectAPI getTaskDetails = new MLAGetTasksBySubjectAPI(MLAEditTaskFragment.this.getActivity());
        getTaskDetails.execute();

    }

    void getIntentService() {
        Intent previous = MLAEditTaskFragment.this.getActivity().getIntent();
        Bundle bundle = previous.getExtras();
    }


    class MLAGetTasksBySubjectAPI extends AsyncTask<Void, Void, List<MLATaskDetails>> {
        Context context;
        String subjectId = "";

        public MLAGetTasksBySubjectAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            subjectId = spinnerSubject.getSelectedItem().toString();
        }


        @Override
        protected void onPostExecute(List<MLATaskDetails> taskDetails2) {
            if (taskDetails2 != null) {
                taskDetails = taskDetails2;
                taskUpdateAdapter = new MLATaskUpdateAdapter(context, false, taskDetails);
                listViewTasks.setAdapter(taskUpdateAdapter);
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

                strSubjectId = new String[subjectDetails.size()];
                int position = 0;
                for (int i = 0; i < subjectDetails.size(); i++) {
                    strSubjectId[i] = subjectDetails.get(i).idSubject;
                    if (strSubjectId[i].equalsIgnoreCase(subIdSel)) {
                        position = i;
                    }
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, strSubjectId);
                spinnerSubject.setAdapter(arrayAdapter);
                spinnerSubject.setSelection(position);
            } else {

            }
        }

        @Override
        protected List<MLASubjectDetails> doInBackground(Void... params) {
            try {
                Call<List<MLASubjectDetails>> callSubjectData = Api.getClient().getAllSubject();
                Response<List<MLASubjectDetails>> responseSubjectData = callSubjectData.execute();
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

    Predicate<MLASubjectDetails> filterPredicate = new Predicate<MLASubjectDetails>() {
        public boolean apply(MLASubjectDetails obj) {
            if (prefsManager.getStringData("userType").equals(UserTypeData.ADMIN)) {
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
