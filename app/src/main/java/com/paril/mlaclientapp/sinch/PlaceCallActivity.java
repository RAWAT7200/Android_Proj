package com.paril.mlaclientapp.sinch;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.paril.mlaclientapp.R;
import com.paril.mlaclientapp.ui.activity.BaseActivity;
import com.sinch.android.rtc.MissingPermissionException;
import com.sinch.android.rtc.calling.Call;

public class PlaceCallActivity extends BaseActivity {

    private Button mCallButton;
    private TextView mCallName;
    public static final String CALLER_NAME = "CALLER_NAME";

    public static final String IS_VIDEO_CALL = "IS_VIDEO_CALL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mCallName = (TextView) findViewById(R.id.callName);
        mCallName.setText(getIntent().getStringExtra(CALLER_NAME));
        mCallButton = (Button) findViewById(R.id.callButton);
        mCallButton.setEnabled(false);
        mCallButton.setOnClickListener(buttonClickListener);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setToolbarTitle("Place Call");


        Button stopButton = (Button) findViewById(R.id.stopButton);
        stopButton.setOnClickListener(buttonClickListener);
        if (getIntent().hasExtra(IS_VIDEO_CALL) && getIntent().getBooleanExtra(IS_VIDEO_CALL, false)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
        }
    }

    @Override
    protected void onServiceConnected() {
        mCallButton.setEnabled(true);
    }

    private void stopButtonClicked() {
        if (getSinchServiceInterface() != null) {
            getSinchServiceInterface().stopClient();
        }
        finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void callButtonClicked() {
        String userName = mCallName.getText().toString();
        if (userName.isEmpty()) {
            Toast.makeText(this, "Please enter a user to call", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            Call call = null;

            if (getIntent().hasExtra(IS_VIDEO_CALL) && getIntent().getBooleanExtra(IS_VIDEO_CALL, false)) {
                call = getSinchServiceInterface().callUserVideo(userName);

            } else {
                call = getSinchServiceInterface().callUser(userName);
            }
            if (call == null) {
                // Service failed for some reason, show a Toast and abort
                Toast.makeText(this, "Service is not started. Try stopping the service and starting it again before "
                        + "placing a call.", Toast.LENGTH_LONG).show();
                return;
            }
            String callId = call.getCallId();
            if (getIntent().hasExtra(IS_VIDEO_CALL) && getIntent().getBooleanExtra(IS_VIDEO_CALL, false)) {

                Intent callScreen = new Intent(this, VideoCallScreenActivity.class);
                callScreen.putExtra(SinchService.CALL_ID, callId);
                startActivity(callScreen);
            } else {
                Intent callScreen = new Intent(this, CallScreenActivity.class);
                callScreen.putExtra(SinchService.CALL_ID, callId);
                startActivity(callScreen);
            }
        } catch (MissingPermissionException e) {
            ActivityCompat.requestPermissions(this, new String[]{e.getRequiredPermission()}, 0);
        }

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You may now place a call", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "This application needs permission to use your microphone to function properly.", Toast
                    .LENGTH_LONG).show();
        }
    }

    private OnClickListener buttonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.callButton:
                    callButtonClicked();
                    break;

                case R.id.stopButton:
                    stopButtonClicked();
                    break;

            }
        }
    };
}
