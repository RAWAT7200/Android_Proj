package com.paril.mlaclientapp.ui.fragment;

import android.app.Fragment;
import android.content.Context;
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

import com.paril.mlaclientapp.R;
import com.paril.mlaclientapp.model.MLAStudentDetails;
import com.paril.mlaclientapp.model.MLASubjectDetails;
import com.paril.mlaclientapp.model.MLAUserWithCheckbox;
import com.paril.mlaclientapp.ui.activity.MLAHomeActivity;
import com.paril.mlaclientapp.ui.adapter.MLAUserAdapter;
import com.paril.mlaclientapp.webservice.Api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by paril on 7/25/2017.
 */
public class MLAEnrollStudentViewFragment extends Fragment {
    ListView listViewStudent;
    Spinner spnrSubject;

    // To Store the details of the Students
    List<MLAStudentDetails> studentDetails = new ArrayList<>();
    String[] studentUserName;
    String[] studentName;
    String[] studentEmail;
    MLAUserAdapter userDisplayAdapter;

    // To Store the details of the Subjects
    List<MLASubjectDetails> subjectDetails = new ArrayList<>();
    String[] subjectId;
    String[] subjectTitle;
    String[] subjectDescription;

    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mla_displayenrolledstudents, container, false);
        listViewStudent = (ListView) view.findViewById(R.id.mla_studentenrollview_display_listView);
        spnrSubject = (Spinner) view.findViewById(R.id.mla_studentenrollview_spnrsubjectId);
        listViewStudent.setEmptyView(view.findViewById(R.id.empty_text_view));

        MLAGetAllSubjectAPI mlaGetAllSubjectAPI = new MLAGetAllSubjectAPI(this.getActivity());
        mlaGetAllSubjectAPI.execute();

        spnrSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MLAGetEnrollBySubjectAPI mlaGetEnrollBySubjectAPI = new MLAGetEnrollBySubjectAPI(MLAEnrollStudentViewFragment.this.getActivity());
                mlaGetEnrollBySubjectAPI.execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }


    class MLAGetAllSubjectAPI extends AsyncTask<Void, Void, List<MLASubjectDetails>> {
        Context context;

        public MLAGetAllSubjectAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            ((MLAHomeActivity) getActivity()).showProgressDialog("Fetching Subjects...");
        }

        @Override
        protected void onPostExecute(List<MLASubjectDetails> listSubjectDetail) {

            ((MLAHomeActivity) getActivity()).hideProgressDialog();
            subjectDetails = new ArrayList<>();
            if (listSubjectDetail != null && listSubjectDetail.size() > 0) {
                subjectDetails = listSubjectDetail;
                subjectId = new String[subjectDetails.size()];
                subjectTitle = new String[subjectDetails.size()];
                subjectDescription = new String[subjectDetails.size()];
                for (int i = 0; i < subjectDetails.size(); i++) {
                    subjectId[i] = subjectDetails.get(i).idSubject;
                    subjectTitle[i] = subjectDetails.get(i).title;
                    subjectTitle[i] += "    (" + subjectDetails.get(i).idSubject + ")";
                    subjectDescription[i] = subjectDetails.get(i).description;
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, subjectId);
                spnrSubject.setAdapter(arrayAdapter);
            } else {
                ((MLAHomeActivity) getActivity()).showSnackBar("There are no subjects.", getView().findViewById(R.id.fragment_enroll_display_coordinatorLayout));
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

    class MLAGetEnrollBySubjectAPI  extends AsyncTask<Void, Void, List<MLAStudentDetails>> {
        Context context;
        String subj = "";

        public MLAGetEnrollBySubjectAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            ((MLAHomeActivity) getActivity()).showProgressDialog("Fetching Students ...");
            subj = spnrSubject.getSelectedItem().toString();
        }

        @Override
        protected void onPostExecute(List<MLAStudentDetails> apiResponse) {
            ((MLAHomeActivity) getActivity()).hideProgressDialog();

            if (apiResponse != null) {
                studentDetails = apiResponse;
                studentUserName = new String[studentDetails.size()];
                studentName = new String[studentDetails.size()];
                studentEmail = new String[studentDetails.size()];
                for (int i = 0; i < studentDetails.size(); i++) {
                    studentUserName[i] = studentDetails.get(i).idStudent;
                    studentName[i] = studentDetails.get(i).lastName + ", " + studentDetails.get(i).firstName;
                    studentName[i] += "    (" + studentDetails.get(i).idStudent + ")";
                    studentEmail[i] = studentDetails.get(i).emailId;
                }
                List<MLAUserWithCheckbox> listUserDisplayCheckb=new ArrayList<>();
                for (int i = 0; i < studentDetails.size(); i++) {
                    final MLAUserWithCheckbox usersDisplayProvider = new MLAUserWithCheckbox(studentName[i], studentEmail[i], false, studentUserName[i]);
                    listUserDisplayCheckb.add(usersDisplayProvider);
                }
                userDisplayAdapter = new MLAUserAdapter(context,listUserDisplayCheckb);
                listViewStudent = (ListView) view.findViewById(R.id.mla_studentenrollview_display_listView);
                listViewStudent.setAdapter(userDisplayAdapter);
            } else {
                userDisplayAdapter = new MLAUserAdapter(context,new ArrayList<MLAUserWithCheckbox>()) ;
                listViewStudent = (ListView) view.findViewById(R.id.mla_studentenrollview_display_listView);
                listViewStudent.setAdapter(userDisplayAdapter);
            }
        }

        @Override
        protected List<MLAStudentDetails> doInBackground(Void... params) {
            try {
                Call<List<MLAStudentDetails>> callStudentData = Api.getClient().getEnrollBySub(subj);
                Response<List<MLAStudentDetails>> responseStudentData = callStudentData.execute();
                if (responseStudentData.isSuccessful() && responseStudentData.body() != null) {
                    return responseStudentData.body();
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
}
