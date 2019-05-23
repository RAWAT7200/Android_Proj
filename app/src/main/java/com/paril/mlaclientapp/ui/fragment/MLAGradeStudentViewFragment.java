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
import android.widget.TextView;

import com.paril.mlaclientapp.R;
import com.paril.mlaclientapp.model.MLAGradeTask;
import com.paril.mlaclientapp.model.MLARegisterUsers;
import com.paril.mlaclientapp.model.MLAStudentsGrade;
import com.paril.mlaclientapp.model.MLASubjectDetails;
import com.paril.mlaclientapp.ui.adapter.MLAGradeStudentAdapter;
import com.paril.mlaclientapp.util.PrefsManager;
import com.paril.mlaclientapp.webservice.Api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by paril on 7/23/2017.
 */
public class MLAGradeStudentViewFragment extends Fragment {
    List<MLASubjectDetails> subjectDetails = new ArrayList<MLASubjectDetails>();
    String[] idTasks;
    String[] topic;
    String[] description;
    String[] studentId;
    String[] instrGrade;

    String[] idSubject;
    String[] titleSubject;
    String[] descriptionSubject;
    PrefsManager prefsManager;


    Spinner spinnerSubject;

    List<MLAGradeTask> gradeDetails = new ArrayList<MLAGradeTask>();

    ListView listViewTasks;
    MLAGradeStudentAdapter mlaGradeStudentAdapter;

    private TextView txtAvgGrade;
    View view;
    List<String> gradeList;
    final float[] gradeAmt = new float[]{4.0f, 3.7f, 3.3f, 3.0f, 2.7f, 2.3f, 2.0f, 1.7f, 1.3f, 1.0f, 0.7f, 0.0f};

    MLARegisterUsers register;

    // extract the extras that was sent from the previous intent
    void getExtra() {
        Intent previous = MLAGradeStudentViewFragment.this.getActivity().getIntent();
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
        view = inflater.inflate(R.layout.fragment_mla_studentgradedisplay, container, false);
        spinnerSubject = (Spinner) view.findViewById(R.id.Mla_task_subjct_spinner);
        listViewTasks = (ListView) view.findViewById(R.id.mla_task_display_listView);
        listViewTasks.setEmptyView(view.findViewById(R.id.empty_text_view));
        register = new MLARegisterUsers();
        final String[] grades = new String[]{"A (4.0)", "A- (3.7)", "B+ (3.3)", "B (3.0)", "B- (2.7)", "C+ (2.3)", "C (2.0)", "C- (1.7)", "D+ (1.3)", "D (1.0)", "D- (0.7)", "F - (0.0)", "Not Graded"};

        gradeList = Arrays.asList(grades);

        prefsManager = new PrefsManager(getActivity());
        txtAvgGrade = (TextView) view.findViewById(R.id.fragment_display_grade_txtTotalGrade);
        // extract the extras that was sent from the previous intent
        getExtra();

        MLAGetSubjectByStudentAPI mlaGetSubjectByStudentAPI = new MLAGetSubjectByStudentAPI(this.getActivity());
        mlaGetSubjectByStudentAPI.execute();

        spinnerSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                txtAvgGrade.setText("Not Available");

                MLAGetGradeDetailsAPI mlaGetGradeDetailsAPI= new MLAGetGradeDetailsAPI(MLAGradeStudentViewFragment.this.getActivity());
                mlaGetGradeDetailsAPI.execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    //this class will get the grade details and add them to listview item by item
    class MLAGetGradeDetailsAPI extends AsyncTask<Void, Void, List<MLAGradeTask>> {
        Context context;
        String subjectId2 = "";
        String stdId = "";

        public MLAGetGradeDetailsAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            subjectId2 = spinnerSubject.getSelectedItem().toString();
            stdId = register.userName;
        }

        //this method will be called after the doInBackground get all the admin users from the webapi
        @Override
        protected void onPostExecute(List<MLAGradeTask> taskDetails2) {
            //check if the call to api passed
            if (taskDetails2 != null) {
                gradeDetails = new ArrayList<MLAGradeTask>();
                for (int i = 0; i < taskDetails2.size(); i++) {
                    if (isTaskFinished(taskDetails2.get(i).getSchedule_endTime())) {
                        gradeDetails.add(taskDetails2.get(i));
                    }
                }

                idTasks = new String[gradeDetails.size()];
                topic = new String[gradeDetails.size()];
                description = new String[gradeDetails.size()];
                studentId = new String[gradeDetails.size()];
                instrGrade = new String[gradeDetails.size()];
                for (int i = 0; i < gradeDetails.size(); i++) {
                    idTasks[i] = gradeDetails.get(i).idTask + "";
                    topic[i] = gradeDetails.get(i).topic;
                    description[i] = gradeDetails.get(i).description;
                    studentId[i] = gradeDetails.get(i).student_id;
                    instrGrade[i] = gradeDetails.get(i).instr_grade;
                }

                mlaGradeStudentAdapter = new MLAGradeStudentAdapter(context, R.layout.rowlayout_mla_grade);
                listViewTasks.setAdapter(mlaGradeStudentAdapter);
                for (int i = 0; i < gradeDetails.size(); i++) {
                    MLAStudentsGrade gradeStdDisplayProvider = new MLAStudentsGrade(idTasks[i].toString(), topic[i].toString(), instrGrade[i].toString());
                    mlaGradeStudentAdapter.add(gradeStdDisplayProvider);
                }

                if (gradeDetails != null && gradeDetails.size() > 0) {
                    float gradeAvg = 0f;
                    boolean hasOneGrade = false;
                    int totalData = 0;
                    for (int i = 0; i < gradeDetails.size(); i++) {
                        int index = gradeList.indexOf(gradeDetails.get(i).instr_grade);
                        if (index != -1 && index != gradeList.size() - 1) {
                            totalData++;
                            gradeAvg = gradeAvg + gradeAmt[index];

                            hasOneGrade = true;

                        }

                    }
                    if (hasOneGrade) {
                        gradeAvg = gradeAvg / totalData;

                        for (int i = 0; i < gradeAmt.length; i++) {
                            if (gradeAvg >= gradeAmt[i]) {
                                txtAvgGrade.setText(gradeList.get(i));
                                break;
                            }
                        }
                    }

                }
            }
        }

        @Override
        protected List<MLAGradeTask> doInBackground(Void... params) {
            try {
                Call<List<MLAGradeTask>> callSubjectData = Api.getClient().getGradesForStudent(stdId, subjectId2);
                Response<List<MLAGradeTask>> responseSubjectData = callSubjectData.execute();
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


    //this class will get the subject ids and add then to the spinner
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
                idSubject = new String[subjectDetails.size()];
                titleSubject = new String[subjectDetails.size()];
                descriptionSubject = new String[subjectDetails.size()];
                for (int i = 0; i < subjectDetails.size(); i++) {
                    idSubject[i] = subjectDetails.get(i).idSubject;
                    titleSubject[i] = subjectDetails.get(i).title;
                    titleSubject[i] += "    (" + subjectDetails.get(i).idSubject + ")";

                    descriptionSubject[i] = subjectDetails.get(i).description;
                }
                //add the subject ids to the spinner
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, idSubject);
                spinnerSubject.setAdapter(arrayAdapter);
            } else {

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
