package com.paril.mlaclientapp.ui.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.paril.mlaclientapp.R;
import com.paril.mlaclientapp.model.MLAInstructorDetails;
import com.paril.mlaclientapp.model.MLASubjectDetails;
import com.paril.mlaclientapp.ui.fragment.MLAEnrollStudentFragment;
import com.paril.mlaclientapp.util.CommonUtils;
import com.paril.mlaclientapp.util.PrefsManager;
import com.paril.mlaclientapp.util.UserTypeData;
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

public class MLAViewSubjectActivity extends BaseActivity {
    EditText txtSubjectId;
    EditText txtSubjectTitle;
    EditText txtSubjectDescription;
    Spinner spnrInstructorId;
    EditText txtAliasMailId;
    MLASubjectDetails subjectDetails;
    boolean isToAdd = false, enabledEditMode = false;
    List<MLAInstructorDetails> instDetails = new ArrayList<MLAInstructorDetails>();
    PrefsManager prefsManager;
    String[] instId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mla_subjectdisplay);
        isToAdd = getIntent().getBooleanExtra(CommonUtils.EXTRA_IS_TO_ADD, false);
        prefsManager = new PrefsManager(this);
        enabledEditMode = getIntent().getBooleanExtra(CommonUtils.EXTRA_EDIT_MODE, false);
        txtSubjectId = (EditText) findViewById(R.id.mla_subject_txtSubjectid);
        txtSubjectTitle = (EditText) findViewById(R.id.mla_subject_txtSubjecttitle);
        txtSubjectDescription = (EditText) findViewById(R.id.mla_subject_txtSubjectdescription);
        spnrInstructorId = (Spinner) findViewById(R.id.mla_subject_spnrInstructorId);
        txtAliasMailId = (EditText) findViewById(R.id.mla_subject_txtmailAlias);

        MLAGetAllInstructorAPI getUserDetails = new MLAGetAllInstructorAPI(this);
        getUserDetails.execute();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        spnrInstructorId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    MLAInstructorDetails a = instDetails.get(position);
                    System.out.println(a.aliasMailId);
                    txtAliasMailId.setText(a.aliasMailId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        txtAliasMailId.setEnabled(false);

    }

    private void setUpData() {
        txtSubjectId.setText(subjectDetails.getIdSubject());
        txtSubjectTitle.setText(subjectDetails.getTitle());
        txtSubjectDescription.setText(subjectDetails.getDescription());
        for (int i = 0; i < instDetails.size(); i++) {
            if (instDetails.get(i).getIdInstructor().equalsIgnoreCase(subjectDetails.getIdInstructor())) {
                spnrInstructorId.setSelection(i);
                break;
            }
        }
        txtAliasMailId.setText(subjectDetails.getMailingAlias());
        spnrInstructorId.setEnabled(false);
        txtSubjectId.setEnabled(false);
    }


    private void enableFields(boolean makeEditable) {
        txtSubjectTitle.setEnabled(makeEditable);
        txtSubjectDescription.setEnabled(makeEditable);
        //txtAliasMailId.setEnabled(makeEditable);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (prefsManager.getStringData("userType").equals(UserTypeData.STUDENT)) {
            menu.findItem(R.id.menu_edit).setVisible(false);
            menu.findItem(R.id.menu_cancel).setVisible(false);
            menu.findItem(R.id.menu_save).setVisible(false);

        } else if (!isToAdd) {
            if (enabledEditMode) {
                menu.findItem(R.id.menu_edit).setVisible(false);
                menu.findItem(R.id.menu_cancel).setVisible(true);
                menu.findItem(R.id.menu_save).setVisible(true);
            } else {
                menu.findItem(R.id.menu_edit).setVisible(true);
                menu.findItem(R.id.menu_cancel).setVisible(false);
                menu.findItem(R.id.menu_save).setVisible(false);
            }
        } else {
            menu.findItem(R.id.menu_edit).setVisible(false);
            menu.findItem(R.id.menu_cancel).setVisible(false);
            menu.findItem(R.id.menu_save).setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.menu_edit) {
            enabledEditMode = true;
            invalidateOptionsMenu();
            enableFields(enabledEditMode);
        } else if (item.getItemId() == R.id.menu_cancel) {
            enabledEditMode = false;
            invalidateOptionsMenu();
            enableFields(enabledEditMode);
            setUpData();
        } else if (item.getItemId() == R.id.menu_save) {
            hideKeyboard();

            if (isToAdd) {

                if (txtSubjectId.getText().toString().equals("") ||
                        txtSubjectTitle.getText().toString().equals("") ||
                        txtSubjectDescription.getText().toString().equals("")) {

                    showSnackBar(getString(R.string.enter_all_fields), findViewById(R.id.activity_view_subject_coordinatorLayout));

                } else if (!CommonUtils.isValidMail(txtAliasMailId.getText().toString())) {
                    txtAliasMailId.requestFocus();
                    showSnackBar(getString(R.string.invalid_email_id), findViewById(R.id.activity_view_subject_coordinatorLayout));

                } else {
                    if (CommonUtils.checkInternetConnection(MLAViewSubjectActivity.this)) {
                        MLAAddSubjectAPI addSubjectTask = new MLAAddSubjectAPI(MLAViewSubjectActivity.this);
                        addSubjectTask.execute();
                    } else {
                        showSnackBar(getString(R.string.check_connection), findViewById(R.id.activity_view_subject_coordinatorLayout));
                    }
                }
            } else {

            }
            if (txtSubjectId.getText().toString().equals("") || txtSubjectTitle.getText().toString().equals("") || txtSubjectDescription.getText().toString().equals("")) {
                showSnackBar(getString(R.string.enter_all_fields), findViewById(R.id.activity_view_subject_coordinatorLayout));
            } else if (!CommonUtils.isValidMail(txtAliasMailId.getText().toString())) {
                txtAliasMailId.requestFocus();
                showSnackBar(getString(R.string.invalid_email_id), findViewById(R.id.activity_view_subject_coordinatorLayout));
            } else {
                if (CommonUtils.checkInternetConnection(MLAViewSubjectActivity.this)) {
                    MLAUpdateSubjectAPI updateSubjectTask = new MLAUpdateSubjectAPI(MLAViewSubjectActivity.this);
                    updateSubjectTask.execute();
                } else {
                    showSnackBar(getString(R.string.check_connection), findViewById(R.id.activity_view_subject_coordinatorLayout));
                }
            }

        }

        return super.onOptionsItemSelected(item);
    }


    class MLAUpdateSubjectAPI extends AsyncTask<Void, Void, String> {
        Context context;
        private MLASubjectDetails subjectDetails;

        // The static parameters will be removed in the future from the WebAPI.
        // Right now, I have set all the parameters according to the iOS application.

        public MLAUpdateSubjectAPI(Context ctx) {
            context = ctx;
            subjectDetails = new MLASubjectDetails();
            subjectDetails.idSubject = txtSubjectId.getText().toString();
            subjectDetails.title = txtSubjectTitle.getText().toString();
            subjectDetails.description = txtSubjectDescription.getText().toString();
            subjectDetails.videoEnabled = "n";
            subjectDetails.audioEnabled = "y";
            subjectDetails.startTime = "05:13:09";
            subjectDetails.endTime = "2017-10-18T05:13:09";
            subjectDetails.idInstructor = spnrInstructorId.getSelectedItem().toString();
            subjectDetails.startDate = "2016-10-18T05:13:09";
            subjectDetails.endDate = "2017-10-18T05:13:09";
            subjectDetails.mailingAlias = txtAliasMailId.getText().toString();
            subjectDetails.duration = 0;
        }

        @Override
        protected void onPreExecute() {
            showProgressDialog("Update Subject Data...");
        }

        @Override
        protected void onPostExecute(String statusCode) {
            hideProgressDialog();
            if (statusCode.equals("202"))
            {
                finish();
            } else {
                showSnackBar(getString(R.string.server_error), findViewById(R.id.activity_view_subject_coordinatorLayout));

            }
        }

        @Override
        protected String doInBackground(Void... params) {

            Call<String> callUpdate = Api.getClient().updateSubject(subjectDetails);
            try {
                Response<String> respUpdate = callUpdate.execute();
                return "202";
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }
    }


    class MLAAddSubjectAPI extends AsyncTask<Void, Void, String> {
        Context context;
        MLASubjectDetails subjectDetails = new MLASubjectDetails();
        public MLAAddSubjectAPI(Context ctx) {
            context = ctx;
        }

        // The static parameters will be removed in the future from the WebAPI.
        // Right now, I have set all the parameters according to the iOS application.
        @Override
        protected void onPreExecute() {
            subjectDetails.idSubject = txtSubjectId.getText().toString();
            subjectDetails.title = txtSubjectTitle.getText().toString();
            subjectDetails.description = txtSubjectDescription.getText().toString();
            subjectDetails.videoEnabled = "n";
            subjectDetails.audioEnabled = "y";
            subjectDetails.startTime = "05:13:09";
            subjectDetails.endTime = "2017-10-18T05:13:09";
            subjectDetails.idInstructor = spnrInstructorId.getSelectedItem().toString();
            subjectDetails.startDate = "2016-10-18T05:13:09";
            subjectDetails.endDate = "2017-10-18T05:13:09";
            subjectDetails.mailingAlias = txtAliasMailId.getText().toString();
            subjectDetails.duration = 0;
            showProgressDialog("Adding Subject...");
        }

        @Override
        protected void onPostExecute(String statusCode) {
            hideProgressDialog();
            if (statusCode.equals("202")) //the item is created
            {
                finish();
            } else {
                showSnackBar(getString(R.string.server_error), findViewById(R.id.activity_view_subject_coordinatorLayout));
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            Call<MLASubjectDetails> callAddSubject = Api.getClient().addSubject(subjectDetails);
            try {
                Response<MLASubjectDetails> respCallSubject = callAddSubject.execute();
                if (respCallSubject.isSuccessful())
                    return "202";
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }
    }

    class MLAGetAllInstructorAPI extends AsyncTask<Void, Void, List<MLAInstructorDetails>> {
        Context context;

        public MLAGetAllInstructorAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            showProgressDialog("Getting Instructor User Data...");
        }

        @Override
        protected void onPostExecute(List<MLAInstructorDetails> userDetails) {

            hideProgressDialog();
            if (userDetails != null) {
                instDetails = userDetails;
                instId = new String[instDetails.size()];

                for (int i = 0; i < instDetails.size(); i++) {
                    instId[i] = instDetails.get(i).idInstructor;
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MLAViewSubjectActivity.this, android.R.layout.simple_spinner_dropdown_item, instId);

                spnrInstructorId.setAdapter(adapter);
                if (!isToAdd) {
                    subjectDetails = (MLASubjectDetails) getIntent().getSerializableExtra(CommonUtils.EXTRA_USER_ADMIN_DATA);
                    setToolbarTitle(subjectDetails.getTitle());
                    setUpData();
                    enableFields(enabledEditMode);
                } else {
                    setToolbarTitle("Add Subject");
                }
            } else {

                showSnackBar(getString(R.string.server_error), findViewById(R.id.activity_view_subject_coordinatorLayout));
            }
        }

        @Override
        protected List<MLAInstructorDetails> doInBackground(Void... params) {

            try {
                Call<List<MLAInstructorDetails>> callInstUserData = Api.getClient().getInstructors();
                Response<List<MLAInstructorDetails>> responseInstUser = callInstUserData.execute();
                if (responseInstUser.isSuccessful() && responseInstUser.body() != null) {
                    return responseInstUser.body();
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
