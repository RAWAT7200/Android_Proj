package com.paril.mlaclientapp.ui.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.internal.util.Predicate;
import com.paril.mlaclientapp.R;
import com.paril.mlaclientapp.model.MLAScheduleDetailPostData;
import com.paril.mlaclientapp.model.MLAStudentDetails;
import com.paril.mlaclientapp.model.MLASubjectDetails;
import com.paril.mlaclientapp.service.AlertTaskIntentService;
import com.paril.mlaclientapp.ui.activity.MLAHomeActivity;
import com.paril.mlaclientapp.util.PrefsManager;
import com.paril.mlaclientapp.util.UserTypeData;
import com.paril.mlaclientapp.webservice.Api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Time;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by paril on 7/17/2017.
 */
public class MLAScheduleAddFragment extends Fragment {
    View view;

    TextView txtstartDay, txtChooseDay;
    TextView txtendDay;
    TextView txtstartTime;
    TextView txtendTime;
    Spinner spnrSubjectID;

    String[] strSubjectId;
    String[] strSubjectTitle;
    String[] strSubjectDescription;

    String strStartDay;
    String strEndDay;
    String strStartTime;
    String strEndTime;

    int scheduleStartYear;
    int scheduleStartMonth;
    int scheduleStartDay;
    int scheduleStartHour;
    int scheduleStartMinute;

    int scheduleEndYear;
    int scheduleEndMonth;
    int scheduleEndDay;
    int scheduleEndHour;
    int scheduleEndMinute;

    boolean isAvailableForSchedule = true;

    String[] strUserNames;
    String[] strFirstLastNameUser;
    String[] strEmailUser;

    List<MLASubjectDetails> subjectDetails = new ArrayList<MLASubjectDetails>();
    boolean[] checkedDays = new boolean[]{
            false,
            false,
            false,
            false,
            false,
            false,
            false
    };

    PrefsManager prefsManager;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 101: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    ((MLAHomeActivity) getActivity()).showSnackBar("Please grant permission otherwise calendar events can not be added.", view.findViewById(R.id.fragment_add_schedule_coordinatorLayout));
                }
                return;
            }

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mla_addschedule, container, false);
        setHasOptionsMenu(true);
        prefsManager = new PrefsManager(getActivity());
        spnrSubjectID = (Spinner) view.findViewById(R.id.mla_addschedule_spnrSubjectId);
        txtstartDay = (TextView) view.findViewById(R.id.mla_addschedule_txtStartDay);
        txtendDay = (TextView) view.findViewById(R.id.mla_addschedule_txtEndDay);
        txtstartTime = (TextView) view.findViewById(R.id.mla_addschedule_txtStartTime);
        txtendTime = (TextView) view.findViewById(R.id.mla_addschedule_txtEndTime);
        txtChooseDay = (TextView) view.findViewById(R.id.mla_addschedule_txtChooseDay);
        txtChooseDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDaysDialog();
            }
        });

        txtstartDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int mothe = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        txtstartDay.setText((1 + monthOfYear) + "/" + dayOfMonth + "/" + year);
                        strStartDay = (1 + monthOfYear) + "/" + dayOfMonth + "/" + year;
                        scheduleStartYear = year;
                        scheduleStartMonth = monthOfYear;
                        scheduleStartDay = dayOfMonth;

                    }
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(MLAScheduleAddFragment.this.getActivity(), android.R.style.Theme_DeviceDefault_Dialog, onDateSetListener, year, mothe, day);
                datePickerDialog.setTitle("Start Day");
                datePickerDialog.show();
            }
        });
        txtendDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int mothe = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        txtendDay.setText((1 + monthOfYear) + "/" + dayOfMonth + "/" + year);
                        strEndDay = (1 + monthOfYear) + "/" + dayOfMonth + "/" + year;
                        scheduleEndYear = year;
                        scheduleEndMonth = monthOfYear;
                        scheduleEndDay = dayOfMonth;
                    }
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(MLAScheduleAddFragment.this.getActivity(), android.R.style.Theme_DeviceDefault_Dialog, onDateSetListener, year, mothe, day);
                datePickerDialog.setTitle("End Day");
                datePickerDialog.show();
            }
        });
        txtstartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        txtstartTime.setText(getTimeInAmPm(hourOfDay, minute));
                        strStartTime = getTimeInAmPm(hourOfDay, minute);
                        scheduleStartHour = hourOfDay;
                        scheduleStartMinute = minute;
                    }
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(MLAScheduleAddFragment.this.getActivity(), android.R.style.Theme_DeviceDefault_Dialog, onTimeSetListener, hour, minute, android.text.format.DateFormat.is24HourFormat(MLAScheduleAddFragment.this.getActivity()));
                timePickerDialog.setTitle("Start Time");
                timePickerDialog.show();

            }
        });
        txtendTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        txtendTime.setText(getTimeInAmPm(hourOfDay, minute));
                        strEndTime = getTimeInAmPm(hourOfDay, minute);
                        scheduleEndHour = hourOfDay;
                        scheduleEndMinute = minute;
                    }
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(MLAScheduleAddFragment.this.getActivity(), android.R.style.Theme_DeviceDefault_Dialog, onTimeSetListener, hour, minute, android.text.format.DateFormat.is24HourFormat(MLAScheduleAddFragment.this.getActivity()));
                timePickerDialog.setTitle("End Time");
                timePickerDialog.show();
            }
        });

        MLAGetAllSubjectWithTaskAPI getAllSubjectWithTaskAPI = new MLAGetAllSubjectWithTaskAPI(this.getActivity());
        getAllSubjectWithTaskAPI.execute();

        return view;
    }


    private void showDaysDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_DeviceDefault_Dialog);

        String[] days = new String[]{
                "Monday",
                "Tuesday",
                "Wednesday",
                "Thursday",
                "Friday",
                "Saturday",
                "Sunday"
        };


        final List<String> daysList = Arrays.asList(days);

        builder.setMultiChoiceItems(days, checkedDays, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                checkedDays[which] = isChecked;
            }
        });


        builder.setCancelable(false);
        builder.setTitle("Every");


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                txtChooseDay.setText("");
                for (int i = 0; i < checkedDays.length; i++) {
                    boolean checked = checkedDays[i];
                    if (checked) {
                        txtChooseDay.setText(txtChooseDay.getText() + daysList.get(i) + " ");
                    }
                }

                if (txtChooseDay.getText().toString().trim().equalsIgnoreCase("")) {
                    txtChooseDay.setText("Choose days");
                }
            }
        });



        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();

        dialog.show();
    }

    private String getTimeInAmPm(int hour, int minute) {
        Time time = new Time(hour, minute, 0);
        Format formatter;
        formatter = new SimpleDateFormat("hh:mm:ss a", Locale.US);
        return formatter.format(time);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_schedule_add, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_schedule_add) {
            addSchedule();
        }
        return true;
    }

    private void addSchedule() {

        if (!isAvailableForSchedule)
            return;

        if (spnrSubjectID.getSelectedItem().toString().equals("") ||
                txtstartDay.getText().toString().equals("") ||
                txtstartDay.getText().toString().equals("Choose start day") ||
                txtendDay.getText().toString().equals("") ||
                txtendDay.getText().toString().equals("Choose end day") ||
                txtstartTime.getText().toString().equals("") ||
                txtstartTime.getText().toString().equals("Choose start time") ||
                txtendTime.getText().toString().equals("") ||
                txtendTime.getText().toString().equals("Choose end time") ||
                txtChooseDay.getText().toString().equals("Choose days")) {

                ((MLAHomeActivity) getActivity()).showSnackBar("Please choose data for all inputs.", view.findViewById(R.id.fragment_add_schedule_coordinatorLayout));

        } else {

            MLAGetEnrollBySubjectAPI getEnrollBySubjectAPI = new MLAGetEnrollBySubjectAPI(MLAScheduleAddFragment.this.getActivity());
            getEnrollBySubjectAPI.execute();

        }

    }


    private String getSelectedDaysCalendar() {
        String[] data = txtChooseDay.getText().toString().split(" ");
        String days = "";
        for (int i = 0; i < data.length; i++) {
            if (data[i].equalsIgnoreCase("monday")) {
                days = days + "," + "MO";
            } else if (data[i].equalsIgnoreCase("tuesday")) {
                days = days + "," + "TU";
            } else if (data[i].equalsIgnoreCase("wednesday")) {
                days = days + "," + "WE";
            } else if (data[i].equalsIgnoreCase("thursday")) {
                days = days + "," + "TH";
            } else if (data[i].equalsIgnoreCase("friday")) {
                days = days + "," + "FR";
            } else if (data[i].equalsIgnoreCase("saturday")) {
                days = days + "," + "SA";
            } else if (data[i].equalsIgnoreCase("sunday")) {
                days = days + "," + "SU";
            }

        }

        if (days.startsWith(",")) {
            days = days.replaceFirst(",", "");
        }
        return days;
    }

    private String getSelectedDays() {
        String[] data = txtChooseDay.getText().toString().split(" ");
        String days = "";
        for (String day :
                data) {
            if (day.equalsIgnoreCase("monday")) {
                days = days + "m";
            } else if (day.equalsIgnoreCase("tuesday")) {
                days = days + "t";
            } else if (day.equalsIgnoreCase("wednesday")) {
                days = days + "w";
            } else if (day.equalsIgnoreCase("thursday")) {
                days = days + "r";
            } else if (day.equalsIgnoreCase("friday")) {
                days = days + "f";
            } else if (day.equalsIgnoreCase("saturday")) {
                days = days + "s";
            } else if (day.equalsIgnoreCase("sunday")) {
                days = days + "u";
            }

        }
        return days;
    }

    class MLAGetEnrollBySubjectAPI extends AsyncTask<Void, Void, String> {
        Context context;
        String idSubject1;
        String every1;
        String startDate1;
        String endDate1;


        public MLAGetEnrollBySubjectAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {

            idSubject1 = spnrSubjectID.getSelectedItem().toString();
            every1 = getSelectedDays();
            startDate1 = strStartDay + " " + strStartTime;
            endDate1 = strEndDay + " " + strEndTime;
            ((MLAHomeActivity) getActivity()).showProgressDialog("Adding Schedule and Tasks...");
        }

        @Override
        protected void onPostExecute(String statusCode) {

            ((MLAHomeActivity) getActivity()).hideProgressDialog();

            if (statusCode.equals("created")) //the item is created
            {
                String email_list = "";
                for (int i = 0; i < strEmailUser.length; i++) {
                    if ((i + 1) < strEmailUser.length)
                        email_list += strEmailUser[i] + " , ";
                    else
                        email_list += strEmailUser[i];
                }

                String st_h = scheduleEndHour + "";
                String st_m = scheduleEndMinute + "";
                String st_month = (scheduleEndMonth + 1) + "";
                String st_d = scheduleEndDay + "";
                if ((scheduleEndMonth + 1) < 10) {
                    st_month = "0" + (scheduleEndMonth + 1);
                }
                if (scheduleEndDay < 10) {
                    st_d = "0" + (scheduleEndDay);
                }
                if (scheduleEndHour < 10) {
                    st_h = "0" + (scheduleStartHour);
                }
                if (scheduleEndMinute < 10) {
                    st_m = "0" + (scheduleStartMinute);
                }
                String rpt = getSelectedDaysCalendar();

                Calendar beginTime = Calendar.getInstance();
                beginTime.set(scheduleStartYear, scheduleStartMonth, scheduleStartDay, scheduleStartHour, scheduleStartMinute);
                Calendar endTime = Calendar.getInstance();
                endTime.set(scheduleStartYear, scheduleStartMonth, scheduleStartDay, scheduleEndHour, scheduleEndMinute);

                try {
                    ContentResolver cr = getActivity().getContentResolver();
                    ContentValues values = new ContentValues();
                    values.put(CalendarContract.Events.DTSTART, beginTime.getTimeInMillis());
                    values.put(CalendarContract.Events.DTEND, endTime.getTimeInMillis());
                    values.put(CalendarContract.Events.TITLE, subjectDetails.get(spnrSubjectID.getSelectedItemPosition()).title);
                    values.put(CalendarContract.Events.DESCRIPTION, subjectDetails.get(spnrSubjectID.getSelectedItemPosition()).description);
                    values.put(CalendarContract.Events.CALENDAR_ID, 1);
                    values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=" + scheduleEndYear + st_month + st_d + "T" + st_h + st_m + "00Z;WKST=SU;BYDAY=" + rpt);
                    values.put(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
                    TimeZone timeZone = TimeZone.getDefault();
                    values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());

                    if (Build.VERSION.SDK_INT < 23 || (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED)) {
                        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
                    }


                    AlertDialog.Builder builder=new AlertDialog.Builder(getActivity(),android.R.style.Theme_DeviceDefault_Dialog );
                    builder.setTitle(getString(R.string.app_name));
                    builder.setMessage("Would you like to add task Now or Later?");
                    builder.setPositiveButton("Add Now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dialogInterface.dismiss();
                            ( (MLAHomeActivity)getActivity()).setToolbarTitle("Edit Schedule");

                            MLAEditTaskFragment updateTaskFragment = new MLAEditTaskFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("subId", idSubject1);

                            updateTaskFragment.setArguments(bundle);


                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.mla_fragmentholder, updateTaskFragment);
                            fragmentTransaction.commit();
                        }
                    });
                    builder.setNegativeButton("Later", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ( (MLAHomeActivity)getActivity()).onHomeClick();

                           dialogInterface.dismiss();

                        }
                    });
                    builder.create().show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ((MLAHomeActivity) getActivity()).showSnackBar("The schedule and tasks are created.", view.findViewById(R.id.fragment_add_schedule_coordinatorLayout));

                clearFields();
                final Intent intentService = new Intent(getActivity(), AlertTaskIntentService.class);
                getActivity().startService(intentService);
            } else {
                ((MLAHomeActivity) getActivity()).showSnackBar("The schedule and tasks are not created as it might already have existing tasks.", view.findViewById(R.id.fragment_add_schedule_coordinatorLayout));

            }

        }

        @Override
        protected String doInBackground(Void... params) {
            final MLAScheduleDetailPostData scheduleDetails = new MLAScheduleDetailPostData();
            scheduleDetails.setSubjectId(idSubject1);
            scheduleDetails.setInstructorId("");
            scheduleDetails.setTopic("");
            scheduleDetails.setDescription("");
            scheduleDetails.setScheduleStartTime(startDate1);
            scheduleDetails.setScheduleEndTime(endDate1);
            scheduleDetails.setIsQuiz("y");
            try {
                scheduleDetails.setRepeatTask(every1);
                Call<String> callPostSchedule = Api.getClient().addSchedule(scheduleDetails);
                Response<String> resPostSchedule = callPostSchedule.execute();
                if (resPostSchedule.code() == 201) {

                    Call<List<MLAStudentDetails>> callStudentData = Api.getClient().getEnrollBySub(idSubject1);
                    Response<List<MLAStudentDetails>> responseStudentData = callStudentData.execute();
                    if (responseStudentData.isSuccessful() && responseStudentData.body() != null) {
                        List<MLAStudentDetails> studentUserDetails = responseStudentData.body();
                        if (studentUserDetails != null) {
                            strUserNames = new String[studentUserDetails.size()];
                            strFirstLastNameUser = new String[studentUserDetails.size()];
                            strEmailUser = new String[studentUserDetails.size()];
                            for (int i = 0; i < studentUserDetails.size(); i++) {
                                strUserNames[i] = studentUserDetails.get(i).userName;
                                strFirstLastNameUser[i] = studentUserDetails.get(i).lastName + ", " + studentUserDetails.get(i).firstName;
                                strFirstLastNameUser[i] += "    (" + studentUserDetails.get(i).userName + ")";

                                strEmailUser[i] = studentUserDetails.get(i).emailId;
                            }
                        }
                    }
                } else {
                    return "";
                }


            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
            return "created";
        }
    }

    private void clearFields() {
        txtendTime.setText("Choose end time");
        txtstartTime.setText("Choose start time");
        txtstartDay.setText("Choose start day");
        txtendDay.setText("Choose end day");
        txtChooseDay.setText("Choose days");
    }

    class MLAGetAllSubjectWithTaskAPI extends AsyncTask<Void, Void, List<MLASubjectDetails>> {
        Context context;

        public MLAGetAllSubjectWithTaskAPI(Context ctx) {
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
                    strSubjectId = new String[subjectDetails.size()];
                    strSubjectTitle = new String[subjectDetails.size()];
                    strSubjectDescription = new String[subjectDetails.size()];
                    for (int i = 0; i < subjectDetails.size(); i++) {
                        strSubjectId[i] = subjectDetails.get(i).idSubject;
                        strSubjectTitle[i] = subjectDetails.get(i).title;
                        strSubjectTitle[i] += "    (" + subjectDetails.get(i).idSubject + ")";

                        strSubjectDescription[i] = subjectDetails.get(i).description;
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, strSubjectId);
                    spnrSubjectID.setAdapter(arrayAdapter);
                    if (Build.VERSION.SDK_INT >= 23) {
                        MLAScheduleAddFragment.this.requestPermissions(new String[]{android.Manifest.permission.WRITE_CALENDAR}, 101);
                    }
                } else {
                    ((MLAHomeActivity) getActivity()).showSnackBar("There are no subjects for adding schedule.", getView().findViewById(R.id.fragment_add_schedule_coordinatorLayout));
                    isAvailableForSchedule = false;

                }

            } else {
                ((MLAHomeActivity) getActivity()).showSnackBar("There are no subjects for adding schedule.", getView().findViewById(R.id.fragment_add_schedule_coordinatorLayout));

            }
        }

        @Override
        protected List<MLASubjectDetails> doInBackground(Void... params) {

            try {
                Call<List<MLASubjectDetails>> callSubjectData = Api.getClient().getAllSubjectWithTask("false"); // False key is used, it indicates that we need to fetch all the subjects which does not have any schedule. So, we can add a schedule for it.
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
            if (prefsManager.getStringData("userType").equals(UserTypeData.ADMIN)) {
                return true;
            } else if (prefsManager.getStringData("userType").equals(UserTypeData.INSTRUCTOR) && obj.getIdInstructor().equals(prefsManager.getStringData("userName"))) {
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

}
