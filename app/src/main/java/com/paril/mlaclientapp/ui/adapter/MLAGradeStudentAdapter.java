package com.paril.mlaclientapp.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.paril.mlaclientapp.R;
import com.paril.mlaclientapp.model.MLAStudentsGrade;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paril on 7/23/2017.
 */
public class MLAGradeStudentAdapter extends ArrayAdapter {
    List list = new ArrayList<>();
    public MLAGradeStudentAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;
        rowView = convertView;
        MLADataAdapter dataAdapter;
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.rowlayout_mla_grade_student,parent,false);
            dataAdapter = new MLADataAdapter();
            dataAdapter.txtTopic = (TextView) rowView.findViewById(R.id.mla_gradestudent_txtTopic);
            dataAdapter.txtGrade = (TextView) rowView.findViewById(R.id.mla_gradestudent_txtGrade);
            rowView.setTag(dataAdapter);
            rowView.setTag(R.id.mla_gradestudent_txtTopic, dataAdapter.txtTopic);
            rowView.setTag(R.id.mla_gradestudent_txtGrade, dataAdapter.txtGrade);
        }
        else
        {
            dataAdapter = (MLADataAdapter) rowView.getTag();
        }
        MLAStudentsGrade studentsGrade;
        studentsGrade = (MLAStudentsGrade) this.getItem(position);
        if (TextUtils.isEmpty(studentsGrade.getGrade())) {
            dataAdapter.txtGrade .setText("Not Graded");

        } else {
            dataAdapter.txtGrade .setText(studentsGrade.getGrade());
        }
        if (TextUtils.isEmpty(studentsGrade.getTopic())) {
            dataAdapter.txtTopic .setText("Not Available");
        } else {
            dataAdapter.txtTopic .setText(studentsGrade.getTopic());
        }
        return rowView;
    }
    static class MLADataAdapter
    {
        TextView txtGrade;
        TextView txtTopic;
    }
    @Override
    public Object getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public void add(Object object) {
        super.add(object);
        list.add(object);
    }
}