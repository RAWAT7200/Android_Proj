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

import com.android.internal.util.Predicate;
import com.paril.mlaclientapp.R;
import com.paril.mlaclientapp.model.MLAStudentDetails;
import com.paril.mlaclientapp.model.MLASubjectDetails;
import com.paril.mlaclientapp.model.MLAUserWithCheckbox;
import com.paril.mlaclientapp.ui.activity.MLAHomeActivity;
import com.paril.mlaclientapp.ui.adapter.MLAUserAdapter;
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
 * Created by paril on 7/25/2017.
 */
public class MLAInstructStudentViewFragment extends Fragment {
    ListView listViewStudent;

    List<MLAStudentDetails> userDetails = new ArrayList<MLAStudentDetails>();
    String[] userName;
    String[] user_FLnames;
    String[] user_Email;
    MLAUserAdapter userDisplayAdapter;

    List<MLASubjectDetails> subjectDetails = new ArrayList<MLASubjectDetails>();
    String[] idSubject;
    String[] titleSubject;
    String[] descriptionSubject;

    Spinner spinnerSubject;

    PrefsManager prefsManager;

    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mla_displayenrolledstudents, container, false);
        listViewStudent = (ListView) view.findViewById(R.id.mla_studentenrollview_display_listView);
        spinnerSubject = (Spinner) view.findViewById(R.id.mla_studentenrollview_spnrsubjectId);
        listViewStudent.setEmptyView(view.findViewById(R.id.empty_text_view));
        prefsManager = new PrefsManager(getActivity());

        MLAGetAllSubjectAPI mlaGetAllSubjectAPI = new MLAGetAllSubjectAPI(this.getActivity());
        mlaGetAllSubjectAPI.execute();

        spinnerSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MLAGetEnrollBySubjectAPI mlaGetEnrollBySubjectAPI = new MLAGetEnrollBySubjectAPI(MLAInstructStudentViewFragment.this.getActivity());
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
            ((MLAHomeActivity) getActivity()).showProgressDialog("Getting Subject Data...");

        }

        @Override
        protected void onPostExecute(List<MLASubjectDetails> listSubjectDetail) {

            ((MLAHomeActivity) getActivity()).hideProgressDialog();

            if (listSubjectDetail != null && listSubjectDetail.size() > 0) {
                subjectDetails = new ArrayList<>();
                subjectDetails = filter(listSubjectDetail, filterPredicate);
                if (subjectDetails.size() > 0) {
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
                }else{
                    ((MLAHomeActivity) getActivity()).showSnackBar("There are no subjects assigned.", getView().findViewById(R.id.fragment_enroll_display_coordinatorLayout));

                }

            } else {
                ((MLAHomeActivity) getActivity()).showSnackBar("There are no subjects assigned.", getView().findViewById(R.id.fragment_enroll_display_coordinatorLayout));

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
            if (prefsManager.getStringData("userType").equals(UserTypeData.INSTRUCTOR) && obj.getIdInstructor().equals(prefsManager.getStringData("userName"))) {
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

    class MLAGetEnrollBySubjectAPI extends AsyncTask<Void, Void, List<MLAStudentDetails>> {
        Context context;
        String subj = "";

        public MLAGetEnrollBySubjectAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            ((MLAHomeActivity) getActivity()).showProgressDialog("Getting Student Data...");
            subj = spinnerSubject.getSelectedItem().toString();

        }

        //this method will be called after the doInBackground get all the student users from the webapi
        @Override
        protected void onPostExecute(List<MLAStudentDetails> userDetails2) {
            ((MLAHomeActivity) getActivity()).hideProgressDialog();

            //check if the call to api passed
            if (userDetails2 != null) {
                userDetails = userDetails2;
                userName = new String[userDetails.size()];
                user_FLnames = new String[userDetails.size()];
                user_Email = new String[userDetails.size()];
                for (int i = 0; i < userDetails.size(); i++) {
                    userName[i] = userDetails.get(i).idStudent;
                    user_FLnames[i] = userDetails.get(i).lastName + ", " + userDetails.get(i).firstName;
                    user_FLnames[i] += "    (" + userDetails.get(i).idStudent + ")";

                    user_Email[i] = userDetails.get(i).emailId;
                }
                List<MLAUserWithCheckbox> listUserDisplayCheckb = new ArrayList<MLAUserWithCheckbox>();
                for (int i = 0; i < userDetails.size(); i++) {
                    final MLAUserWithCheckbox usersDisplayProvider = new MLAUserWithCheckbox(user_FLnames[i], user_Email[i], false, userName[i]);
                    listUserDisplayCheckb.add(usersDisplayProvider);
                }
                //fill the items in the listview with a customized adapter
                userDisplayAdapter = new MLAUserAdapter(context, listUserDisplayCheckb);
                listViewStudent = (ListView) view.findViewById(R.id.mla_studentenrollview_display_listView);
                listViewStudent.setAdapter(userDisplayAdapter);

            } else {
                userDisplayAdapter = new MLAUserAdapter(context, new ArrayList<MLAUserWithCheckbox>());
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


