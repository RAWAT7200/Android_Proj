package com.paril.mlaclientapp.ui.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.paril.mlaclientapp.R;
import com.paril.mlaclientapp.sinch.SinchService;
import com.paril.mlaclientapp.util.PrefsManager;
import com.sinch.android.rtc.SinchError;

/**
 * Created by paril on 02-Oct-17.
 */

public class BaseActivity extends AppCompatActivity implements ServiceConnection, SinchService.StartFailedListener {

    private SinchService.SinchServiceInterface mSinchServiceInterface;
    PrefsManager prefsManager;

    @Override
    public void onStarted() {

    }

    @Override
    public void onStartFailed(SinchError error) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getApplicationContext().bindService(new Intent(this, SinchService.class), this,
                BIND_AUTO_CREATE);
        receiver = new FinishReceiver();
        prefsManager = new PrefsManager(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(ACTION_FINISH));


    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (SinchService.class.getName().equals(componentName.getClassName())) {
            mSinchServiceInterface = (SinchService.SinchServiceInterface) iBinder;
            onServiceConnected();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        if (SinchService.class.getName().equals(componentName.getClassName())) {
            mSinchServiceInterface = null;
            onServiceDisconnected();
        }
    }

    protected void onServiceConnected() {
        getSinchServiceInterface().setStartListener(this);
        if (!prefsManager.getStringData("userName").equalsIgnoreCase("")) {
            if (!getSinchServiceInterface().isStarted()) {
                getSinchServiceInterface().startClient(prefsManager.getStringData("userName"));
            }
        }


        // for subclasses
    }

    protected void onServiceDisconnected() {
        // for subclasses
    }

    protected SinchService.SinchServiceInterface getSinchServiceInterface() {
        return mSinchServiceInterface;
    }


    public void setToolbarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    private ProgressDialog progressDialog;

    public static String ACTION_FINISH = "ACTION_FINISH";

    private FinishReceiver receiver;

    public class FinishReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            finish();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    public void showSnackBar(String message, View view) {
        Snackbar snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_LONG);

        snackbar.show();
    }

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

    public void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
