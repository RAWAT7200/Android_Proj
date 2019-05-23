package com.paril.mlaclientapp.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.paril.mlaclientapp.R;
import com.paril.mlaclientapp.model.MLAGradeTask;
import com.paril.mlaclientapp.webservice.Api;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by paril on 7/25/2017.
 */

public class MLAUpdateGradeActivity extends BaseActivity {
    private MLAGradeTask gradeTask;
    TextView txtSubId, txtStdId, txtTaskTime, txtGrade;

    private int gradeSelPos = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mla_updategrade);
        gradeTask = (MLAGradeTask) getIntent().getSerializableExtra("GradeData");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setToolbarTitle("Update Grade");

        txtGrade = (TextView) findViewById(R.id.activity_update_grade_txtGrade);

        txtStdId = (TextView) findViewById(R.id.activity_update_grade_txtStudentId);

        txtSubId = (TextView) findViewById(R.id.activity_update_grade_txtSubId);

        txtTaskTime = (TextView) findViewById(R.id.activity_update_grade_txtTaskTime);
        txtSubId.setText(gradeTask.getSubject_id());
        txtStdId.setText(gradeTask.getStudent_id());
        txtTaskTime.setText(getDateString(gradeTask.getSchedule_startTime(), gradeTask.getSchedule_endTime()));
        if (gradeTask.getInstr_grade() != null && !gradeTask.getInstr_grade().trim().equalsIgnoreCase("")) {
            txtGrade.setText(gradeTask.getInstr_grade());
        }else{
            txtGrade.setText("Not Graded");
        }
        txtGrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDaysDialog();
            }
        });
    }

    private void showDaysDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MLAUpdateGradeActivity.this, android.R.style.Theme_DeviceDefault_Dialog);

        // String array for alert dialog multi choice items
        final CharSequence[] grades = new CharSequence[]{"A (4.0)", "A- (3.7)", "B+ (3.3)", "B (3.0)", "B- (2.7)", "C+ (2.3)", "C (2.0)", "C- (1.7)", "D+ (1.3)", "D (1.0)", "D- (0.7)", "F - (0.0)", "Not Graded"};
        gradeSelPos=grades.length-1;
        if (gradeTask != null) {
            for (int i = 0; i < grades.length; i++) {
                if (grades[i].toString().equalsIgnoreCase(gradeTask.getInstr_grade())) {
                    gradeSelPos = i;
                    break;
                }
            }
        }

        builder.setSingleChoiceItems(grades, gradeSelPos, null);

        // Specify the dialog is not cancelable
        builder.setCancelable(false);

        // Set a title for alert dialog
        builder.setTitle("Select a Grade");

        // Set the positive/yes button click listener
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do something when click positive button
                dialog.dismiss();
                int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                txtGrade.setText(grades[selectedPosition]);
            }
        });


        // Set the neutral/cancel button click listener
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do something when click the neutral button
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private String getDateString(String startDateString, String endDateString) {
        Calendar calendar = null;
        String durationString = "";
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

            Date date = dateFormat.parse(startDateString);

            calendar = Calendar.getInstance();
            calendar.setTime(date);
            String dateString = calendar.get(Calendar.DATE) > 9 ? calendar.get(Calendar.DATE) + "" : "0" + calendar.get(Calendar.DATE);
            String monthString = (calendar.get(Calendar.MONTH) + 1) > 9 ? ((calendar.get(Calendar.MONTH) + 1) + "") : ("0" + (calendar.get(Calendar.MONTH) + 1)
            );
            String hourString = calendar.get(Calendar.HOUR_OF_DAY) > 9 ? calendar.get(Calendar.HOUR_OF_DAY) + "" : ("0" + calendar.get(Calendar.HOUR_OF_DAY));
            String minuteString = calendar.get(Calendar.MINUTE) > 9 ? calendar.get(Calendar.MINUTE) + "" : ("0" + calendar.get(Calendar.MINUTE));
            Date endDate = dateFormat.parse(endDateString);
            calendar.setTime(endDate);
            String endHourString = calendar.get(Calendar.HOUR_OF_DAY) > 9 ? calendar.get(Calendar.HOUR_OF_DAY) + "" : ("0" + calendar.get(Calendar.HOUR_OF_DAY));
            String endMinuteString = calendar.get(Calendar.MINUTE) > 9 ? calendar.get(Calendar.MINUTE) + "" : ("0" + calendar.get(Calendar.MINUTE));

            durationString = monthString + "/" + dateString + "/" + calendar.get(Calendar.YEAR) + "," + hourString + ":" + minuteString + "-" + endHourString + ":" + endMinuteString;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return durationString;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_schedule_add, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.menu_item_schedule_add) {
            if (txtGrade.getText().toString().equalsIgnoreCase("Select Grade")) {
                showSnackBar("Please select grade first to update.", findViewById(R.id.activity_update_grade_coordinatorLayout));

            } else {
                MLAUpdateGradeAPI updateGrade = new MLAUpdateGradeAPI(MLAUpdateGradeActivity.this);
                updateGrade.execute();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    class MLAUpdateGradeAPI extends AsyncTask<Void, Void, String> {
        Context context;
        String grade = "";

        public MLAUpdateGradeAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            grade = txtGrade.getText().toString().equalsIgnoreCase("Not Graded")?"":txtGrade.getText().toString();
            showProgressDialog("Updating Grade...");
        }

        @Override
        protected void onPostExecute(String statusCode) {
            hideProgressDialog();
            if (statusCode.equals("302")) {
                showSnackBar("Grade has been updated.", findViewById(R.id.activity_update_grade_coordinatorLayout));
                MLAUpdateGradeActivity.this.finish();
            } else {
                showSnackBar("Error while updating grade. Please try again.", findViewById(R.id.activity_update_grade_coordinatorLayout));
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                Call<String> callGradTask = Api.getClient().updateGrade(gradeTask.getIdTask(), gradeTask.getStudent_id(), grade);
                Response<String> resGradTask = callGradTask.execute();
                if (resGradTask != null) {
                    return resGradTask.code() + "";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return "";
        }
    }
}
