package com.paril.mlaclientapp.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.paril.mlaclientapp.ui.activity.MLAHomeActivity;
import com.paril.mlaclientapp.R;

/**
 * Created by paril on 28-Sep-17.
 */

public class MLAHomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_mla_myinformation,container,false);

        View view = inflater.inflate(R.layout.fragment_home,container,false);

        final Button btnCourse=(Button)view.findViewById(R.id.fragment_home_btnCourses);
        btnCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MLAHomeActivity)getActivity()).openCourse();
            }
        });


        final Button btnInstruction=(Button)view.findViewById(R.id.fragment_home_btnInstructions);

        btnInstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MLAHomeActivity)getActivity()).openInstruction();

            }
        });

        final Button btnStudents=(Button)view.findViewById(R.id.fragment_home_btnStudents);
        btnStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MLAHomeActivity)getActivity()).openStudent();

            }
        });

        final Button btns=(Button )view.findViewById(R.id.writemsg);
        btns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MLAHomeActivity)getActivity()).openMessage();
            }
        });

        final Button btnss=(Button )view.findViewById(R.id.createmsg);
        btnss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MLAHomeActivity)getActivity()).createMessage();
            }
        });
        return view;

    }
}
