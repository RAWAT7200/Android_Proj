package com.paril.mlaclientapp.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.paril.mlaclientapp.R;
import com.paril.mlaclientapp.model.MLATaskDetails;
import com.paril.mlaclientapp.sinch.PlaceCallActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by paril on 7/22/2017.
 */
public class MLATaskInProgressAdapter extends BaseAdapter {
    List<MLATaskDetails> listTasks = new ArrayList<>();
    Context context;

    public MLATaskInProgressAdapter(Context context, List<MLATaskDetails> listTaks) {
        this.context = context;
        this.listTasks = listTaks;

    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView;
        rowView = convertView;
        MLADataAdapter dataAdapter;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            rowView = inflater.inflate(R.layout.rowlayout_task_inprocess, parent, false);
            dataAdapter = new MLADataAdapter();
            dataAdapter.txtTime = (TextView) rowView.findViewById(R.id.mla_taskinprocess_txtTime);
            dataAdapter.txtTopic = (TextView) rowView.findViewById(R.id.mla_taskinprocess_txtTopic);
            dataAdapter.txtDescription = (TextView) rowView.findViewById(R.id.mla_taskinprocess_txtDescription);
            dataAdapter.btnPause = (Button) rowView.findViewById(R.id.task_item_progress_layout_btnFinish);
            dataAdapter.btnStart = (Button) rowView.findViewById(R.id.task_item_progress_layout_btnStart);
            dataAdapter.imgAudioCall = (ImageView) rowView.findViewById(R.id.task_item_progress_layout_imgAudioCall);
            dataAdapter.imgVideoCall = (ImageView) rowView.findViewById(R.id.task_item_progress_layout_imgVideoCall);
            dataAdapter.progressBar = (ProgressBar) rowView.findViewById(R.id.task_item_progress_layout_Progress);

            rowView.setTag(dataAdapter);
            rowView.setTag(R.id.mla_taskinprocess_txtTime, dataAdapter.txtTime);
            rowView.setTag(R.id.mla_taskinprocess_txtTopic, dataAdapter.txtTopic);
            rowView.setTag(R.id.mla_taskinprocess_txtDescription, dataAdapter.txtDescription);
            rowView.setTag(R.id.task_item_progress_layout_btnFinish, dataAdapter.btnPause);
            rowView.setTag(R.id.task_item_progress_layout_btnStart, dataAdapter.btnStart);
            rowView.setTag(R.id.task_item_progress_layout_imgAudioCall, dataAdapter.imgAudioCall);
            rowView.setTag(R.id.task_item_progress_layout_imgVideoCall, dataAdapter.imgVideoCall);
            rowView.setTag(R.id.task_item_progress_layout_Progress, dataAdapter.progressBar);

        } else {
            dataAdapter = (MLADataAdapter) rowView.getTag();
        }
        final MLATaskDetails taskUpdateProvider;
        taskUpdateProvider = (MLATaskDetails) this.getItem(position);

        if (taskUpdateProvider.isProgressRunning()) {
            Log.d("test", "taskUpdateProvider.getLastProgress()" + taskUpdateProvider.getLastProgress());
            dataAdapter.btnPause.setEnabled(true);
            dataAdapter.btnStart.setEnabled(false);
            dataAdapter.progressBar.setProgress(taskUpdateProvider.getLastProgress());

        } else {
            dataAdapter.btnPause.setEnabled(false);
            dataAdapter.btnStart.setEnabled(true);

        }

        dataAdapter.imgAudioCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent=new Intent(context, PlaceCallActivity.class);
                intent.putExtra(PlaceCallActivity.CALLER_NAME,listTasks.get(position).getInstructor_id());

                intent.putExtra(PlaceCallActivity.IS_VIDEO_CALL,false);
                context.startActivity(intent);

            }
        });
        dataAdapter.imgVideoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent=new Intent(context, PlaceCallActivity.class);
                intent.putExtra(PlaceCallActivity.CALLER_NAME,listTasks.get(position).getInstructor_id());

                intent.putExtra(PlaceCallActivity.IS_VIDEO_CALL,true);
                context.startActivity(intent);


            }
        });
        dataAdapter.btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listTasks.get(position).isProgressRunning = true;
            }
        });
        dataAdapter.btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listTasks.get(position).isProgressRunning = false;
            }
        });
        try {

            dataAdapter.txtTime.setText(getDateString(taskUpdateProvider.getSchedule_startTime(), taskUpdateProvider.getSchedule_endTime()));
        } catch (Exception e) {
            e.printStackTrace();
        }


        dataAdapter.txtTopic.setText(TextUtils.isEmpty(taskUpdateProvider.getTopic()) ? "Not Available" : taskUpdateProvider.getTopic());
        dataAdapter.txtDescription.setText(TextUtils.isEmpty(taskUpdateProvider.getDescription()) ? "Not Available" : taskUpdateProvider.getDescription());

        return rowView;
    }

    static class MLADataAdapter {
        TextView txtTime;
        TextView txtTopic;
        TextView txtDescription;
        Button btnStart, btnPause;

        ImageView imgAudioCall, imgVideoCall;
        ProgressBar progressBar;

    }

    @Override
    public Object getItem(int position) {
        return this.listTasks.get(position);
    }

    @Override
    public int getCount() {
        return this.listTasks.size();
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

    public void updateTaskProgress() {
        if (listTasks != null && listTasks.size() > 0) {
            for (int i = 0; i < listTasks.size(); i++) {
                listTasks.get(i).setLastProgress(calculateProgress(listTasks.get(i).getSchedule_startTime(), listTasks.get(i).getSchedule_endTime()));

            }
            this.notifyDataSetChanged();

        }
    }

    private int calculateProgress(String startDateString, String endDateString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

            Date startDate = dateFormat.parse(startDateString);

            Date endDate = dateFormat.parse(endDateString);

            Date currentDate = new Date();
            long totalTime = (endDate.getTime() - startDate.getTime());
            long currentTime = currentDate.getTime() - startDate.getTime();

            long diff = (currentTime * 100L) / totalTime;

            int percentage = ((int) diff);
            return percentage > 100 ? 100 : percentage;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;

    }
}