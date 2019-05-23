package com.paril.mlaclientapp.ui.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.paril.mlaclientapp.R;
import com.paril.mlaclientapp.ui.activity.MLAHomeActivity;
import com.paril.mlaclientapp.webservice.Api;

import java.io.IOException;
import java.net.MalformedURLException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by paril on 7/22/2017.
 */
public class MLAEditTaskDialog extends DialogFragment {
    View view;
    Button btnSave;
    String idTask;
    EditText topic;
    EditText description;
    static MLAEditTaskFragment taskFragment;

    public static final MLAEditTaskDialog newInstance(MLAEditTaskFragment taskFragmentobj, String idTask, String topic, String description) {
        MLAEditTaskDialog fragment = new MLAEditTaskDialog();
        taskFragment = taskFragmentobj;
        Bundle bundle = new Bundle(2);
        bundle.putString("idTask", idTask);
        bundle.putString("topic", topic);
        bundle.putString("description", description);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mla_edittask, null);
        topic = (EditText) view.findViewById(R.id.mla_updatebox_txtTopic);
        description = (EditText) view.findViewById(R.id.mla_updatebox_txtDescription);
        idTask = getArguments().getString("idTask");
        topic.setText(getArguments().getString("topic"));
        description.setText(getArguments().getString("description"));


        btnSave = (Button) view.findViewById(R.id.mla_updatebox_btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View focusedView=null;
                if (MLAEditTaskDialog.this.topic.hasFocus()) {
                    focusedView = MLAEditTaskDialog.this.topic;
                } else if (MLAEditTaskDialog.this.description.hasFocus()) {
                    focusedView = MLAEditTaskDialog.this.description;
                }
                if (focusedView != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
                }
                MLAUpdateTaskAPI mlaUpdateTaskAPI = new MLAUpdateTaskAPI(MLAEditTaskDialog.this.getActivity());
                mlaUpdateTaskAPI.execute();

            }
        });

        return view;

    }

    class MLAUpdateTaskAPI extends AsyncTask<Void, Void, String> {
        Context context;
        String topic, desc;

        public MLAUpdateTaskAPI(Context ctx) {
            context = ctx;
            topic = MLAEditTaskDialog.this.topic.getText().toString();
            desc = MLAEditTaskDialog.this.description.getText().toString();
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(String statusCode) {
            if (statusCode != null && statusCode.equals("302")) //the password is updated
            {
                ((MLAHomeActivity) MLAEditTaskDialog.this.getActivity()).showSnackBar("Task has been updated", view.findViewById(R.id.fragment_update_task_dialog_coordinatorLayout));
            } else {
                ((MLAHomeActivity) MLAEditTaskDialog.this.getActivity()).showSnackBar("Task has not been updated", view.findViewById(R.id.fragment_update_task_dialog_coordinatorLayout));

            }

            taskFragment.refresh();
            dismiss();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                Call<String> callUpdateTask = Api.getClient().updateTaskData(idTask, topic, desc);
                Response<String> responseUpdatetask = callUpdateTask.execute();
                if (responseUpdatetask.isSuccessful() && responseUpdatetask.body() != null) {
                    return responseUpdatetask.code() + "";
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
}
