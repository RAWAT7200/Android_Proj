package com.paril.mlaclientapp.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.paril.mlaclientapp.R;
import com.paril.mlaclientapp.ui.activity.MLAHomeActivity;
import com.paril.mlaclientapp.util.CommonUtils;
import com.paril.mlaclientapp.webservice.Api;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by paril on 7/16/2017.
 * This class is created to update the user password
 */
public class MLAEditPasswordFragment extends Fragment {
    EditText txtNewPassword;
    EditText txtConfirmPassword;
    View view;
    String userName, password;//the current user name in the system

    void getUserName() {
        Intent previous = this.getActivity().getIntent();
        Bundle bundle = previous.getExtras();
        if (bundle != null) {
            userName = (String) bundle.get("userName");
        }
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_update_password, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_save:
                ((MLAHomeActivity)getActivity()).hideKeyboard();

                if(txtNewPassword.getText().toString().isEmpty() || txtConfirmPassword.getText().toString().isEmpty()){
                    ((MLAHomeActivity) getActivity()).showSnackBar("All fields are mandatory.", getView().findViewById(R.id.fragment_update_password_coordinatorLayout));
                } else if (txtNewPassword.getText().toString().equals(txtConfirmPassword.getText().toString())) {
                    //passwords match, update the database
                    password = txtNewPassword.getText().toString();
                    if (CommonUtils.checkInternetConnection(getActivity())) {
                        MLAUpdatePasswordAPI mlaUpdatePasswordAPI = new MLAUpdatePasswordAPI (MLAEditPasswordFragment.this.getActivity());
                        mlaUpdatePasswordAPI.execute();
                    }else{
                        ((MLAHomeActivity) getActivity()).showSnackBar(getString(R.string.check_connection), getView().findViewById(R.id.fragment_update_password_coordinatorLayout));
                    }
                } else {

                    ((MLAHomeActivity) getActivity()).showSnackBar("Both Passwords do not match", getView().findViewById(R.id.fragment_update_password_coordinatorLayout));
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mla_changepassword, container, false);
        txtNewPassword = (EditText) view.findViewById(R.id.mla_editpwd_txtNewPassword);
        txtConfirmPassword = (EditText) view.findViewById(R.id.mla_editpwd_txtConfirmPassword);
        getUserName();
        return view;
    }

    //this class is needed to update the password without blocking the UI. once update the password, it will show poup message
    class MLAUpdatePasswordAPI extends AsyncTask<Void, Void, String> {
        Context context;

        public MLAUpdatePasswordAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {

            ((MLAHomeActivity) getActivity()).showProgressDialog("Updating Password...");
        }

        @Override
        protected void onPostExecute(String statusCode) {
            if (statusCode.equals("302")) //the password is updated
            {
                txtNewPassword.setText("");
                txtConfirmPassword.setText("");
                ((MLAHomeActivity) getActivity()).hideProgressDialog();
                ((MLAHomeActivity) getActivity()).showSnackBar("Password has been changed.", getView().findViewById(R.id.fragment_update_password_coordinatorLayout));

            } else {
                ((MLAHomeActivity) getActivity()).hideProgressDialog();
                ((MLAHomeActivity) getActivity()).showSnackBar("Password has not been updated.Please try again.", getView().findViewById(R.id.fragment_update_password_coordinatorLayout));
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                Call<String> callUpdatePass = Api.getClient().changePassword(userName, password);
                Response<String> response=callUpdatePass.execute();
                return "" + response.code();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }
    }
}
