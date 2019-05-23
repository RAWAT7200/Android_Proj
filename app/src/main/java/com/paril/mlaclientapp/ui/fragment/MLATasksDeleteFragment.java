package com.paril.mlaclientapp.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.internal.util.Predicate;
import com.daimajia.swipe.util.Attributes;
import com.paril.mlaclientapp.R;
import com.paril.mlaclientapp.model.MLASubjectDetails;
import com.paril.mlaclientapp.service.AlertTaskIntentService;
import com.paril.mlaclientapp.ui.activity.MLAHomeActivity;
import com.paril.mlaclientapp.ui.adapter.MLASubjectAdapter;
import com.paril.mlaclientapp.ui.adapter.OnItemClickListener;
import com.paril.mlaclientapp.ui.view.EmptyRecyclerView;
import com.paril.mlaclientapp.util.PrefsManager;
import com.paril.mlaclientapp.util.UserTypeData;
import com.paril.mlaclientapp.util.VerticalSpaceItemDecoration;
import com.paril.mlaclientapp.webservice.Api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by paril on 7/26/2017.
 */
public class MLATasksDeleteFragment extends Fragment {
    EmptyRecyclerView listView;
    List<MLASubjectDetails> subjectDetails = new ArrayList<MLASubjectDetails>();
    MLASubjectAdapter subjectDisplayAdapter;

    PrefsManager prefsManager;
    int index = -1;
    String selectedSubject = "";

    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mla_tasksdelete, container, false);
        prefsManager=new PrefsManager(getActivity());
        listView = (EmptyRecyclerView) view.findViewById(R.id.mla_subject_display_listView);
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listView.addItemDecoration(new VerticalSpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.divider_list)));

        MLAGetAllSubjectWithTaskAPI getAllSubjectWithTaskAPI = new MLAGetAllSubjectWithTaskAPI(this.getActivity());
        getAllSubjectWithTaskAPI.execute();

        return view;
    }


    class MLAPostTaskRmvAPI extends AsyncTask<Void, Void, String> {
        Context context;

        public MLAPostTaskRmvAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {

            ((MLAHomeActivity) getActivity()).showProgressDialog("Removing task for Subject...");
        }

        @Override
        protected void onPostExecute(String statusCode) {

            ((MLAHomeActivity) getActivity()).hideProgressDialog();
            if (statusCode.equals("302")) //the tasks are deleted
            {
                final Intent intentService = new Intent(getActivity(), AlertTaskIntentService.class);
                getActivity().startService(intentService);
                ((MLAHomeActivity) getActivity()).showSnackBar("Removed all tasks for Subject:"+selectedSubject.toString(), getView().findViewById(R.id.fragment_tasks_delete_coordinatorLayout));
                subjectDetails.remove(index);
                index = -1;
                listView = (EmptyRecyclerView) view.findViewById(R.id.mla_subject_display_listView);
                listView.setLayoutManager(new LinearLayoutManager(getActivity()));

            } else {
                ((MLAHomeActivity) getActivity()).showSnackBar(getString(R.string.server_error), getView().findViewById(R.id.fragment_tasks_delete_coordinatorLayout));
            }
        }

        @Override
        protected String doInBackground(Void... params) {

            Call<String> callDelete = Api.getClient().removeTasks(selectedSubject.toString());
            try {
                Response<String> responseDelete = callDelete.execute();
                if (responseDelete != null) {
                    return responseDelete.code() + "";

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    class MLAGetAllSubjectWithTaskAPI extends AsyncTask<Void, Void, List<MLASubjectDetails>> {
        Context context;

        public MLAGetAllSubjectWithTaskAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            ((MLAHomeActivity) getActivity()).showProgressDialog("Getting Subjects...");
        }

        @Override
        protected void onPostExecute(List<MLASubjectDetails> userDetails2) {
            ((MLAHomeActivity) getActivity()).hideProgressDialog();

            if (userDetails2 != null) {
                subjectDetails = new ArrayList<>();

                subjectDetails = filter(userDetails2, filterPredicate);


                subjectDisplayAdapter = new MLASubjectAdapter(context, subjectDetails, false, new OnItemClickListener<MLASubjectDetails>() {
                    @Override
                    public void onItemClick(MLASubjectDetails item, int resourceId) {
                        Log.d("OnItemClick", "resource:" + resourceId);
                        if (resourceId == R.id.subject_item_display_layout_swipeParent) {
                            selectedSubject = item.idSubject;
                            index = subjectDetails.indexOf(item);

                            AlertDialog.Builder builder=new AlertDialog.Builder(MLATasksDeleteFragment.this.getActivity());
                            builder.setTitle(getString(R.string.app_name));
                            builder.setMessage("Are you sure want to delete tasks for Subject:"+selectedSubject+"?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    MLAPostTaskRmvAPI postTaskRmvAPI = new MLAPostTaskRmvAPI(MLATasksDeleteFragment.this.getActivity());
                                    postTaskRmvAPI.execute();
                                }
                            });
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    index = -1;
                                }
                            });
                            builder.show();
                        }


                    }
                });
                subjectDisplayAdapter.setMode(Attributes.Mode.Single);
                listView.setAdapter(subjectDisplayAdapter);

            }
        }

        @Override
        protected List<MLASubjectDetails> doInBackground(Void... params) {
            try {
                Call<List<MLASubjectDetails>> callSubjectData = Api.getClient().getAllSubjectWithTask("true"); // True key is used, it indicates that we need to fetch all the subjects which has a schedule. So, we can remove a schedule for it.dis

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
            if(prefsManager.getStringData("userType").equals(UserTypeData.ADMIN)){
                return true;
            }
            else if (prefsManager.getStringData("userType").equals(UserTypeData.INSTRUCTOR) && obj.getIdInstructor().equals(prefsManager.getStringData("userName"))) {
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
