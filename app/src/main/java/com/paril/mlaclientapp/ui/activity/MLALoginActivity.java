package com.paril.mlaclientapp.ui.activity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.io.InputStream;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.paril.mlaclientapp.R;
import com.paril.mlaclientapp.model.MLARegisterUsers;
import com.paril.mlaclientapp.util.CommonUtils;
import com.paril.mlaclientapp.util.PrefsManager;
import com.paril.mlaclientapp.webservice.Api;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;


public class MLALoginActivity extends BaseActivity {

    EditText txtUserName;
    EditText txtPassword;
    Button btnLogin;
    MLARegisterUsers register;
    private ProgressDialog progressDialog;
    private PrefsManager prefsManager;


    public void showProgressDialog(String message) {
        if (progressDialog == null || !progressDialog.isShowing()) {
            progressDialog = ProgressDialog.show(this, getString(R.string.app_name), message, true, false);

        }
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();

        }
    }

    void loadingUserInformation() {
        register.userId = prefsManager.getStringData("userId");
        register.userName = prefsManager.getStringData("userName");
        register.userType = prefsManager.getStringData("userType");
    }

    public void showSnackBar(String message, View view) {
        Snackbar snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_LONG);

        snackbar.show();
    }

    void savingUserInformation() {
        prefsManager.saveData("userId", register.userId);
        prefsManager.saveData("userName", register.userName);
        prefsManager.saveData("userType", register.userType);

    }

    void copy(InputStream input, OutputStream output)
    {
        byte[] buffer = new byte[1024*2];

        BufferedInputStream in = new BufferedInputStream(input, 1024*2);
        BufferedOutputStream out = new BufferedOutputStream(output, 1024*2);
        int count = 0, n = 0;
        try {
            while ((n = in.read(buffer, 0, 1024*2)) != -1) {
                out.write(buffer, 0, n);
                count += n;
            }
            output.flush();

                out.close();

                in.close();
            }
            catch (IOException e) {


        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        InputStream in=null;
        try {
            URL url = new URL("https://ec2-13-52-178-229.us-west-1.compute.amazonaws.com/MlaWebApi/");
            URLConnection urlConnection = url.openConnection();
            in = urlConnection.getInputStream();

        }
        catch(Exception e)
        {
            Toast.makeText(getApplicationContext(),"somerrrrrrr",100);
        }
        copy(in,System.out);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("Sign In");
        prefsManager=new PrefsManager(this);
        register = new MLARegisterUsers();

        loadingUserInformation();

      /***Modified code goes here **/

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
       SharedPreferences.Editor editor = prefs.edit();
       editor.putString("userName", register.userName);
       editor.apply();


        if (register.userType != "" && register.userType != null) {
            Intent mlaActivity = new Intent();
            mlaActivity.setClass(MLALoginActivity.this, MLAHomeActivity.class);
            mlaActivity.putExtra("userId", register.userId);
            mlaActivity.putExtra("userName", register.userName);
            mlaActivity.putExtra("userType", register.userType);
            mlaActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mlaActivity);
            finish();
        }

        btnLogin = (Button) findViewById(R.id.mla_login_btnLogin);
        txtUserName = (EditText) findViewById(R.id.mla_login_txtUserName);
        txtPassword = (EditText) findViewById(R.id.mla_login_txtPassword);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                if (TextUtils.isEmpty(txtPassword.getText().toString()) || TextUtils.isEmpty(txtUserName.getText().toString())) {

                    showSnackBar(getString(R.string.enter_all_fields), findViewById(R.id.activity_main_coordinatorLayout));

                } else {
                    if (CommonUtils.checkInternetConnection(MLALoginActivity.this)) {
                        MLALoginAPI authentication = new MLALoginAPI(getApplicationContext());
                        authentication.execute(txtUserName.getText().toString(), txtPassword.getText().toString());
                    } else {
                        showSnackBar(getString(R.string.check_connection), findViewById(R.id.activity_main_coordinatorLayout));
                    }
                }

            }
        });

    }


    class MLALoginAPI extends AsyncTask<String, Void, MLARegisterUsers> {
        Context appContext;

        public MLALoginAPI (Context context) {
            appContext = context;
        }

        @Override
        protected void onPreExecute() {
            showProgressDialog("Verifying Credentials...");
        }

        @Override
        protected void onPostExecute(MLARegisterUsers registerArg) {
            hideProgressDialog();
            register = registerArg;
            if (register.userType != null) {
                Intent mlaActivity = new Intent();
                mlaActivity.setClass(MLALoginActivity.this, MLAHomeActivity.class);
                mlaActivity.putExtra("userId", register.userId);
                mlaActivity.putExtra("userName", register.userName);
                mlaActivity.putExtra("userType", register.userType);
                savingUserInformation();
                mlaActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mlaActivity);
                finish();
            } else {
                showSnackBar("User Name/Password is incorrect. Please enter correct credentials.", findViewById(R.id.activity_main_coordinatorLayout));
            }
        }

        @Override
        protected MLARegisterUsers doInBackground(String... params) {
            MLARegisterUsers register = new MLARegisterUsers();
            Call<List<MLARegisterUsers>> callAuth = Api.getClient().authenticate(params[0], params[1]);
            try {
                Response<List<MLARegisterUsers>> respAuth = callAuth.execute();
                if (respAuth != null && respAuth.isSuccessful() & respAuth.body() != null && respAuth.body().size() > 0) {
                    register = respAuth.body().get(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return register;
        }
    }
}
