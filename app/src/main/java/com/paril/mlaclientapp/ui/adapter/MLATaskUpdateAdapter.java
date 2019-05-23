package com.paril.mlaclientapp.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.paril.mlaclientapp.R;
import com.paril.mlaclientapp.model.MLATaskDetails;
import com.paril.mlaclientapp.sinch.PlaceCallActivity;
import com.paril.mlaclientapp.ui.activity.CallStudentActivity;
import com.paril.mlaclientapp.util.PrefsManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by paril on 7/22/2017.
 */
public class MLATaskUpdateAdapter extends BaseAdapter {
    List<MLATaskDetails> listTasks = new ArrayList<>();
    Context context;
    boolean showCallOptions=false;
    PrefsManager prefsManager;
    public MLATaskUpdateAdapter(Context context,boolean showCallOptions, List<MLATaskDetails> listTaks) {
        this.context = context;
        this.listTasks = listTaks;
        this.showCallOptions=showCallOptions;
        prefsManager=new PrefsManager(context);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View rowView;
        rowView = convertView;
        MLADataAdapter dataAdapter;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            rowView = inflater.inflate(R.layout.rowlayout_mla_taskmodified, parent, false);
            dataAdapter = new MLADataAdapter();
            dataAdapter.txtTime = (TextView) rowView.findViewById(R.id.mla_taskupdte_txtTime);
            dataAdapter.txtTopic = (TextView) rowView.findViewById(R.id.mla_taskupdte_txtTopic);
            dataAdapter.txtDescription = (TextView) rowView.findViewById(R.id.mla_taskupdte_txtDescription);
            dataAdapter.imgAudio = (ImageView) rowView.findViewById(R.id.task_item_update_layout_imgAudioCall);
            dataAdapter.imgVideo = (ImageView) rowView.findViewById(R.id.task_item_update_layout_imgVideoCall);


            rowView.setTag(dataAdapter);
            rowView.setTag(R.id.mla_taskupdte_txtTime, dataAdapter.txtTime);
            rowView.setTag(R.id.mla_taskupdte_txtTopic, dataAdapter.txtTopic);
            rowView.setTag(R.id.mla_taskupdte_txtDescription, dataAdapter.txtDescription);
            rowView.setTag(R.id.task_item_update_layout_imgVideoCall, dataAdapter.imgVideo);
            rowView.setTag(R.id.task_item_update_layout_imgAudioCall, dataAdapter.imgAudio);

        } else {
            dataAdapter = (MLADataAdapter) rowView.getTag();
        }
        final MLATaskDetails taskUpdateProvider;
        taskUpdateProvider = (MLATaskDetails) this.getItem(position);

        try {

            dataAdapter.txtTime.setText(getDateString(taskUpdateProvider.getSchedule_startTime(),taskUpdateProvider.getSchedule_endTime()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(showCallOptions
                ){
            dataAdapter.imgAudio.setVisibility(View.VISIBLE);
            dataAdapter.imgVideo.setVisibility(View.VISIBLE);
        }else{
            dataAdapter.imgAudio.setVisibility(View.GONE
            );
            dataAdapter.imgVideo.setVisibility(View.GONE);

        }
        dataAdapter.imgAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent=new Intent(context, CallStudentActivity.class);
                intent.putExtra(CallStudentActivity.SUBJ_ID,taskUpdateProvider.getSubject_id());
                intent.putExtra(PlaceCallActivity.IS_VIDEO_CALL,false);

                context.startActivity(intent);
            }
        });
        dataAdapter.imgVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent=new Intent(context, CallStudentActivity.class);
                intent.putExtra(CallStudentActivity.SUBJ_ID,taskUpdateProvider.getSubject_id());
                intent.putExtra(PlaceCallActivity.IS_VIDEO_CALL,true);

                context.startActivity(intent);
            }
        });

        dataAdapter.txtTopic.setText(TextUtils.isEmpty(taskUpdateProvider.getTopic()) ? "Not Available" : taskUpdateProvider.getTopic());
        dataAdapter.txtDescription.setText(TextUtils.isEmpty(taskUpdateProvider.getDescription()) ? "Not Available" : taskUpdateProvider.getDescription());

        return rowView;
    }

    static class MLADataAdapter {
        TextView txtTime;
        TextView txtTopic;
        ImageView imgAudio,imgVideo;
        TextView txtDescription;
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
            String monthString = (calendar.get(Calendar.MONTH)+1 )> 9 ? ((calendar.get(Calendar.MONTH)+1) + "") : ("0" + (calendar.get(Calendar.MONTH)+1)
            );
            String hourString = calendar.get(Calendar.HOUR_OF_DAY) > 9 ? calendar.get(Calendar.HOUR_OF_DAY) + "" : ("0" + calendar.get(Calendar.HOUR_OF_DAY));
            String minuteString = calendar.get(Calendar.MINUTE) > 9 ? calendar.get(Calendar.MINUTE) + "" : ("0" + calendar.get(Calendar.MINUTE));
            Date endDate = dateFormat.parse(endDateString);
            calendar.setTime(endDate);
            String endHourString = calendar.get(Calendar.HOUR_OF_DAY) > 9 ? calendar.get(Calendar.HOUR_OF_DAY) + "" : ("0" + calendar.get(Calendar.HOUR_OF_DAY));
            String endMinuteString = calendar.get(Calendar.MINUTE) > 9 ? calendar.get(Calendar.MINUTE) + "" : ("0" + calendar.get(Calendar.MINUTE));

            durationString = monthString+"/"+dateString  + "/" + calendar.get(Calendar.YEAR) + "," + hourString + ":" + minuteString + "-" + endHourString + ":" + endMinuteString;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return durationString;

    }
}