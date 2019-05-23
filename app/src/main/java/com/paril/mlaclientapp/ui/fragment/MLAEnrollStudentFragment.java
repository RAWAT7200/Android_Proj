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
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.paril.mlaclientapp.R;
import com.paril.mlaclientapp.model.MLAStudentDetails;
import com.paril.mlaclientapp.model.MLAStudentEnrollmentPostData;
import com.paril.mlaclientapp.model.MLASubjectDetails;
import com.paril.mlaclientapp.model.MLAUserWithCheckbox;
import com.paril.mlaclientapp.ui.activity.MLAHomeActivity;
import com.paril.mlaclientapp.ui.adapter.MLAUserChckbxAdapter;
import com.paril.mlaclientapp.webservice.Api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by paril on 7/16/2017.
 */
public class MLAEnrollStudentFragment extends Fragment {
    ListView listViewStudent;
    Spinner spnrSubject;
    Button btnEnroll;

    // To Store the details of the Students
    List<MLAStudentDetails> studentDetails = new ArrayList<>();
    ArrayList<String> userNames;
    String[] studentUserName;
    String[] studentName;
    String[] studentEmail;
    MLAUserChckbxAdapter userDisplayAdapter;

    // To Store the details of the Subjects
    List<MLASubjectDetails> subjectDetails = new ArrayList<>();
    String[] subjectId;
    String[] subjectTitle;
    String[] subjectDescription;

    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mla_enrollstudent, container, false);
        listViewStudent = (ListView) view.findViewById(R.id.mla_studentenroll_display_listView);
        listViewStudent.setEmptyView(view.findViewById(R.id.empty_text_view));
        btnEnroll = (Button) view.findViewById(R.id.mla_studentenroll_btnEnroll);
        spnrSubject = (Spinner) view.findViewById(R.id.mla_studentenroll_spnrsubjectId);

        //Flow of the API calls,
        //1.) Fetch all the Subjects.
        //2.) Fetch all the Disenroll Students for the selected subject. By default, the system takes 1st one.
        //3.) Select the students and enrolled them, if needed.

        MLAGetAllSubjectAPI mlaGetAllSubjectAPI = new MLAGetAllSubjectAPI(this.getActivity());
        mlaGetAllSubjectAPI.execute();

        spnrSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //this will call the AsyncTask which will bring the student details and filled them on list
                MLAGetDeEnrollBySubjectAPI mlaGetDeEnrollBySubjectAPI = new MLAGetDeEnrollBySubjectAPI(MLAEnrollStudentFragment.this.getActivity());
                mlaGetDeEnrollBySubjectAPI.execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btnEnroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userDisplayAdapter != null && userDisplayAdapter.getCount() > 0) {
                    // the userDisplayadapter include the boolean values and the userName of the student.
                    int listSize = userDisplayAdapter.getCount();//this is the list size
                    MLAUserWithCheckbox userDisplayCheckbxProvider;
                    userNames = new ArrayList<>();
                    for (int i = 0; i < listSize; i++) {
                        userDisplayCheckbxProvider = (MLAUserWithCheckbox) userDisplayAdapter.getItem(i);
                        if (userDisplayCheckbxProvider.getCheck()) {
                            userNames.add(userDisplayCheckbxProvider.getUserId());
                        }
                    }

                    // if the students are selected from the list then call the API.
                    if(userNames.size()>0){
                        MLAEnrollStudentAPI enrollStudent = new MLAEnrollStudentAPI(MLAEnrollStudentFragment.this.getActivity());
                        enrollStudent.execute();
                    }

                }

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
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, new String[]{});
                spnrSubject.setAdapter(arrayAdapter);
                ((MLAHomeActivity) getActivity()).showSnackBar("There is no subject to enroll.", getView().findViewById(R.id.fragment_enroll_student_coordinatorLayout));
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


    class MLAGetDeEnrollBySubjectAPI extends AsyncTask<Void, Void, List<MLAStudentDetails>> {
        Context context;
        String subj = "";

        public MLAGetDeEnrollBySubjectAPI(Context ctx) {
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
                    List<MLAUserWithCheckbox> listUserDisplayCheckb = new ArrayList<>();
                    for (int i = 0; i < studentDetails.size(); i++) {
                        final MLAUserWithCheckbox usersDisplayProvider = new MLAUserWithCheckbox(studentName[i], studentEmail[i], false, studentUserName[i]);
                        listUserDisplayCheckb.add(usersDisplayProvider);
                    }

                    userDisplayAdapter = new MLAUserChckbxAdapter(context, listUserDisplayCheckb);
                    listViewStudent = (ListView) view.findViewById(R.id.mla_studentenroll_display_listView);
                    listViewStudent.setAdapter(userDisplayAdapter);

            } else {
                userDisplayAdapter = new MLAUserChckbxAdapter(context, new ArrayList<MLAUserWithCheckbox>());
                listViewStudent = (ListView) view.findViewById(R.id.mla_studentenroll_display_listView);
                listViewStudent.setAdapter(userDisplayAdapter);
            }
        }

        @Override
        protected List<MLAStudentDetails> doInBackground(Void... params) {
            try {
                Call<List<MLAStudentDetails>> callStudentData = Api.getClient().getDeEnrollBySub(subj);
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

    class MLAEnrollStudentAPI extends AsyncTask<Void, Void, String> {
        Context context;
        String idSubjectData;
        ArrayList<String> userNameData;

        public MLAEnrollStudentAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            idSubjectData = spnrSubject.getSelectedItem().toString();
            userNameData = userNames;
        }

        @Override
        protected void onPostExecute(String statusCode) {

            if (statusCode.equals("enrolled")) //the item is created
            {
                MLAGetDeEnrollBySubjectAPI mlaGetDeEnrollBySubjectAPI = new MLAGetDeEnrollBySubjectAPI(MLAEnrollStudentFragment.this.getActivity());
                mlaGetDeEnrollBySubjectAPI.execute();
                ((MLAHomeActivity) getActivity()).showSnackBar("The students have been enrolled to the subject.", view.findViewById(R.id.fragment_enroll_student_coordinatorLayout));
            } else {
                ((MLAHomeActivity) getActivity()).showSnackBar("Error while enrolling student.", view.findViewById(R.id.fragment_enroll_student_coordinatorLayout));
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            for (int i = 0; i < userNameData.size(); i++) {
                final MLAStudentEnrollmentPostData postData = new MLAStudentEnrollmentPostData();
                postData.setInstructorId("");
                postData.setSubjectId(idSubjectData);
                postData.setStudentId(userNameData.get(i));
                try {
                    Call<MLAStudentEnrollmentPostData> callEnrollSubjectData = Api.getClient().enrollBySub(postData);
                    Response<MLAStudentEnrollmentPostData> responseSubjectData = callEnrollSubjectData.execute();

                } catch (MalformedURLException e) {
                    return null;

                } catch (IOException e) {
                    return null;
                }
            }
            return "enrolled";
        }
    }

}
