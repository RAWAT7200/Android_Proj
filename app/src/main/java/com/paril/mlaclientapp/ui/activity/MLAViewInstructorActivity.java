package com.paril.mlaclientapp.ui.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.reinaldoarrosi.maskededittext.MaskedEditText;
import com.paril.mlaclientapp.R;
import com.paril.mlaclientapp.model.MLAAdminDetails;
import com.paril.mlaclientapp.model.MLAInstructorDetails;
import com.paril.mlaclientapp.util.CommonUtils;
import com.paril.mlaclientapp.webservice.Api;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by paril on 7/25/2017.
 */

public class MLAViewInstructorActivity extends BaseActivity {
    private MLAInstructorDetails userDetails;
    TextView txtUserId;
    EditText txtUserName;
    EditText txtFirstName;
    EditText txtLastName;
    EditText txtEmailId;
    MaskedEditText txtTelephone;
    EditText txtAliasMailId;
    EditText txtAddress;
    EditText txtHangoutId;
    EditText txtPassword;

    private boolean isToAdd = false;

    private boolean enabledEditMode = false;
    private LinearLayout linUserIdCont, linUserNameCont, linPasswordCont;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mla_instructordisplay);
        isToAdd = getIntent().getBooleanExtra(CommonUtils.EXTRA_IS_TO_ADD, false);

        txtPassword = (EditText) findViewById(R.id.mla_instructor_txtPassword);
        txtUserId = (TextView) findViewById(R.id.mla_instructor_txtUserId);
        txtUserName = (EditText) findViewById(R.id.mla_instructor_txtUserName);
        txtFirstName = (EditText) findViewById(R.id.mla_instructor_txtFirstName);
        txtLastName = (EditText) findViewById(R.id.mla_instructor_txtLastName);
        txtEmailId = (EditText) findViewById(R.id.mla_instructor_txtEmailId);
        txtTelephone = (MaskedEditText) findViewById(R.id.mla_instructor_txtTelephone);
        txtAliasMailId = (EditText) findViewById(R.id.mla_instructor_txtAliasMailId);
        txtAddress = (EditText) findViewById(R.id.mla_instructor_txtAddress);
        txtHangoutId = (EditText) findViewById(R.id.mla_instructor_txtHangoutId);
        linUserIdCont = (LinearLayout) findViewById(R.id.activity_view_admin_linUserId);
        linUserNameCont = (LinearLayout) findViewById(R.id.activity_view_admin_linUserName);
        linPasswordCont = (LinearLayout) findViewById(R.id.activity_view_admin_linPassword);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        if (!isToAdd) {
            userDetails = (MLAInstructorDetails) getIntent().getSerializableExtra(CommonUtils.EXTRA_USER_ADMIN_DATA);

            setToolbarTitle(userDetails.getFirstName() + " " + userDetails.getLastName());
            setUpData();
            enabledEditMode = getIntent().getBooleanExtra(CommonUtils.EXTRA_EDIT_MODE, false);
            enableFields(enabledEditMode);
            linUserIdCont.setVisibility(View.VISIBLE);
            linUserNameCont.setVisibility(View.VISIBLE);
            linPasswordCont.setVisibility(View.GONE);
        } else {

            setToolbarTitle("Add Instructor");
            linUserIdCont.setVisibility(View.GONE);
            txtUserName.setEnabled(true);
            linUserNameCont.setVisibility(View.VISIBLE);
            linPasswordCont.setVisibility(View.VISIBLE);
        }

    }

    private void setUpData() {
        txtUserId.setText("" + userDetails.getUserId());
        txtUserName.setText(userDetails.getIdInstructor());
        txtFirstName.setText(userDetails.getFirstName());
        txtLastName.setText(userDetails.getLastName());
        txtEmailId.setText(userDetails.getEmailId());
        txtTelephone.setText(userDetails.getTelephone());
        txtAliasMailId.setText(userDetails.getAliasMailId());
        txtAddress.setText(userDetails.getAddress());
        txtHangoutId.setText(userDetails.getSkypeId());
    }


    private void enableFields(boolean makeEditable) {
        txtAddress.setEnabled(makeEditable);
        txtAliasMailId.setEnabled(makeEditable);
        txtEmailId.setEnabled(makeEditable);
        txtFirstName.setEnabled(makeEditable);
        txtLastName.setEnabled(makeEditable);
        txtHangoutId.setEnabled(makeEditable);
        txtTelephone.setEnabled(makeEditable);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!isToAdd) {
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

                if (TextUtils.isEmpty(txtAddress.getText().toString()) ||
                        TextUtils.isEmpty(txtUserName.getText().toString()) ||
                        TextUtils.isEmpty(txtEmailId.getText().toString()) ||
                        TextUtils.isEmpty(txtAliasMailId.getText().toString()) ||
                        TextUtils.isEmpty(txtLastName.getText().toString()) ||
                        TextUtils.isEmpty(txtTelephone.getText(true).toString()) ||
                        TextUtils.isEmpty(txtHangoutId.getText().toString()) ||
                        TextUtils.isEmpty(txtFirstName.getText().toString()) ||
                        TextUtils.isEmpty(txtPassword.getText().toString())) {

                    showSnackBar(getString(R.string.enter_all_fields), findViewById(R.id.activity_view_instructor_coordinatorLayout));

                } else if (!CommonUtils.isValidMail(txtEmailId.getText().toString()) ||
                        !CommonUtils.isValidMail(txtAliasMailId.getText().toString()) ||
                        !CommonUtils.isValidMobile(txtTelephone.getText(true).toString())) {

                    if (!CommonUtils.isValidMail(txtEmailId.getText().toString())) {
                        txtEmailId.requestFocus();
                        showSnackBar(getString(R.string.invalid_email_id), findViewById(R.id.activity_view_instructor_coordinatorLayout));

                    } else if (!CommonUtils.isValidMail(txtAliasMailId.getText().toString())) {
                        txtAliasMailId.requestFocus();
                        showSnackBar(getString(R.string.invalid_alt_email_id), findViewById(R.id.activity_view_instructor_coordinatorLayout));

                    } else {
                        txtTelephone.requestFocus();
                        showSnackBar(getString(R.string.invalid_phone_no), findViewById(R.id.activity_view_instructor_coordinatorLayout));

                    }

                } else {
                    if (CommonUtils.checkInternetConnection(MLAViewInstructorActivity.this)) {
                        MLAAddInstructorTask addAdminTask = new MLAAddInstructorTask(MLAViewInstructorActivity.this);
                        addAdminTask.execute();
                    } else {
                        showSnackBar(getString(R.string.check_connection), findViewById(R.id.activity_view_instructor_coordinatorLayout));
                    }
                }
            } else {

                if (TextUtils.isEmpty(txtAddress.getText().toString()) ||
                        TextUtils.isEmpty(txtUserName.getText().toString()) ||
                        TextUtils.isEmpty(txtEmailId.getText().toString()) ||
                        TextUtils.isEmpty(txtAliasMailId.getText().toString()) ||
                        TextUtils.isEmpty(txtLastName.getText().toString()) ||
                        TextUtils.isEmpty(txtTelephone.getText().toString()) ||
                        TextUtils.isEmpty(txtHangoutId.getText().toString()) ||
                        TextUtils.isEmpty(txtFirstName.getText().toString()) ) {

                    showSnackBar(getString(R.string.enter_all_fields), findViewById(R.id.activity_view_instructor_coordinatorLayout));

                } else if (!CommonUtils.isValidMail(txtEmailId.getText().toString()) ||
                        !CommonUtils.isValidMail(txtAliasMailId.getText().toString()) ||
                        !CommonUtils.isValidMobile(txtTelephone.getText(true).toString())) {

                    if (!CommonUtils.isValidMail(txtEmailId.getText().toString())) {
                        txtEmailId.requestFocus();
                        showSnackBar(getString(R.string.invalid_email_id), findViewById(R.id.activity_view_instructor_coordinatorLayout));

                    } else if (!CommonUtils.isValidMail(txtAliasMailId.getText().toString())) {
                        txtAliasMailId.requestFocus();
                        showSnackBar(getString(R.string.invalid_alt_email_id), findViewById(R.id.activity_view_instructor_coordinatorLayout));
                    } else {
                        txtTelephone.requestFocus();
                        showSnackBar(getString(R.string.invalid_phone_no), findViewById(R.id.activity_view_instructor_coordinatorLayout));
                    }

                } else {
                    if (CommonUtils.checkInternetConnection(MLAViewInstructorActivity.this)) {
                        MLAUpdateInstructorAPI updateAdminTask = new MLAUpdateInstructorAPI(MLAViewInstructorActivity.this);
                        updateAdminTask.execute();
                    } else {
                        showSnackBar(getString(R.string.check_connection), findViewById(R.id.activity_view_instructor_coordinatorLayout));
                    }
                }
            }

        }


        return super.onOptionsItemSelected(item);
    }

    class MLAUpdateInstructorAPI extends AsyncTask<Void, Void, String> {
        Context context;
        private MLAInstructorDetails details;

        public MLAUpdateInstructorAPI(Context ctx) {
            context = ctx;
            details = new MLAInstructorDetails();
            details.setAddress(txtAddress.getText().toString());
            details.setAliasMailId(txtAliasMailId.getText().toString());
            details.setEmailId(txtEmailId.getText().toString());
            details.setFirstName(txtFirstName.getText().toString());
            details.setIdInstructor(txtUserName.getText().toString());
            details.setLastName(txtLastName.getText().toString());
            details.setSkypeId(txtHangoutId.getText().toString());
            details.setTelephone(txtTelephone.getText(true).toString());
            details.setUserId(Integer.parseInt(txtUserId.getText().toString()));
        }

        @Override
        protected void onPreExecute() {
            showProgressDialog("Update Instructor User Data...");
        }

        @Override
        protected void onPostExecute(String statusCode) {
            hideProgressDialog();

            if (statusCode.equals("202")) //the item is updated
            {
                finish();
            } else {
                showSnackBar(getString(R.string.server_error), findViewById(R.id.activity_view_instructor_coordinatorLayout));
            }
        }

        @Override
        protected String doInBackground(Void... params) {

            Call<String> callUpdate = Api.getClient().updateInstructor(details);
            try {
                Response<String> respUpdate = callUpdate.execute();
                return "202";
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "";
        }
    }


    class MLAAddInstructorTask extends AsyncTask<Void, Void, String> {
        Context context;
        MLAAdminDetails userDetails = new MLAAdminDetails();

        public MLAAddInstructorTask(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            userDetails.setTelephone(txtTelephone.getText(true).toString());
            userDetails.setIdAdmin(txtUserName.getText().toString());
            userDetails.setSkypeId(txtHangoutId.getText().toString());
            userDetails.setLastName(txtLastName.getText().toString());
            userDetails.setAddress(txtAddress.getText().toString());
            userDetails.setAliasMailId(txtAliasMailId.getText().toString());
            userDetails.setFirstName(txtFirstName.getText().toString());
            userDetails.setEmailId(txtEmailId.getText().toString());
            userDetails.setPassword(txtPassword.getText().toString());
            showProgressDialog("Add Instructor User Data...");
        }

        @Override
        protected void onPostExecute(String statusCode) {
            hideProgressDialog();
            if (statusCode.equals("201")) //the item is created
            {
                finish();
            } else {
                showSnackBar(getString(R.string.server_error), findViewById(R.id.activity_view_instructor_coordinatorLayout));
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            Call<MLAInstructorDetails> callAddAdmin = Api.getClient().addInst(userDetails.getIdAdmin(), userDetails.getPassword(), userDetails.getFirstName(), userDetails.getLastName(), userDetails.getTelephone(), userDetails.getAddress(), userDetails.getAliasMailId(), userDetails.getEmailId(), userDetails.getSkypeId());
            try {
                Response<MLAInstructorDetails> respCallAdmin = callAddAdmin.execute();
                return "" + respCallAdmin.code();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "";
        }
    }
}
