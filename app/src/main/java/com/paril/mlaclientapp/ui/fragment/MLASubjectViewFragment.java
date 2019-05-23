package com.paril.mlaclientapp.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.swipe.util.Attributes;
import com.paril.mlaclientapp.R;
import com.paril.mlaclientapp.model.MLASubjectDetails;
import com.paril.mlaclientapp.ui.activity.MLAHomeActivity;
import com.paril.mlaclientapp.ui.activity.MLAViewSubjectActivity;
import com.paril.mlaclientapp.ui.adapter.OnItemClickListener;
import com.paril.mlaclientapp.ui.adapter.MLASubjectAdapter;
import com.paril.mlaclientapp.util.CommonUtils;
import com.paril.mlaclientapp.util.PrefsManager;
import com.paril.mlaclientapp.util.UserTypeData;
import com.paril.mlaclientapp.util.VerticalSpaceItemDecoration;
import com.paril.mlaclientapp.webservice.Api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by paril on 7/14/2017.
 */
public class MLASubjectViewFragment extends Fragment {

    com.paril.mlaclientapp.ui.view.EmptyRecyclerView recyclerViewSubjects;
    MLASubjectAdapter subjectDisplayAdapter;
    private PrefsManager manager;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mla_subjectslist, container, false);
        recyclerViewSubjects = (com.paril.mlaclientapp.ui.view.EmptyRecyclerView) view.findViewById(R.id.mla_display_subject_recyyclerView);
        recyclerViewSubjects.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewSubjects.addItemDecoration(new VerticalSpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.divider_list)));
        manager = new PrefsManager(getActivity());
        FloatingActionButton btnAddStudent = (FloatingActionButton) view.findViewById(R.id.fragment_display_subject_fabAddUser);

        btnAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(getActivity(), MLAViewSubjectActivity.class);
                intent.putExtra(CommonUtils.EXTRA_IS_TO_ADD, true);
                intent.putExtra(CommonUtils.EXTRA_EDIT_MODE, false);
                startActivity(intent);
            }
        });

        if (manager.getStringData("userType").equals(UserTypeData.STUDENT)) {
            btnAddStudent.setVisibility(View.GONE);
        } else {
            btnAddStudent.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (CommonUtils.checkInternetConnection(getActivity())) {
            MLAGetAllSubjectDetailsAPI getSubjectDetails = new MLAGetAllSubjectDetailsAPI(this.getActivity());
            getSubjectDetails.execute();
        } else {
            ((MLAHomeActivity) getActivity()).showSnackBar(getString(R.string.check_connection), view.findViewById(R.id.fragment_display_subject_coordinatorLayout));
        }

    }

    class MLAGetAllSubjectDetailsAPI extends AsyncTask<Void, Void, List<MLASubjectDetails>> {
        Context context;

        public MLAGetAllSubjectDetailsAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            ((MLAHomeActivity) getActivity()).showProgressDialog("Getting Subject Data...");
        }

        @Override
        protected void onPostExecute(List<MLASubjectDetails> listSubjectDetail) {

            ((MLAHomeActivity) getActivity()).hideProgressDialog();
            List<MLASubjectDetails> listSubjectDetails = new ArrayList<MLASubjectDetails>();
            if (listSubjectDetail != null) {
                listSubjectDetails = listSubjectDetail;
            } else {
                ((MLAHomeActivity) getActivity()).showSnackBar(getString(R.string.server_error), getView().findViewById(R.id.fragment_display_subject_coordinatorLayout));
            }

            subjectDisplayAdapter = new MLASubjectAdapter(context, listSubjectDetails, manager.getStringData("userType").equals(UserTypeData.ADMIN), new OnItemClickListener<MLASubjectDetails>() {
                @Override
                public void onItemClick(MLASubjectDetails item, int resourceId) {
                    Log.d("OnItemClick", "resource:" + resourceId);
                    if (resourceId == R.id.subject_item_display_layout_imgEditUser) {
                        final Intent intent = new Intent(getActivity(), MLAViewSubjectActivity.class);
                        intent.putExtra(CommonUtils.EXTRA_IS_TO_ADD, false);
                        intent.putExtra(CommonUtils.EXTRA_EDIT_MODE, true);
                        intent.putExtra(CommonUtils.EXTRA_USER_ADMIN_DATA, item);
                        startActivity(intent);
                    } else if (resourceId == R.id.subject_item_display_layout_imgDeleteUser) {
                        if (CommonUtils.checkInternetConnection(getActivity())) {
                            MLADeleteSubjectAPI deleteSubjectTask = new MLADeleteSubjectAPI(MLASubjectViewFragment
                                    .this.getActivity());
                            deleteSubjectTask.execute(item.getIdSubject());
                        } else {
                            ((MLAHomeActivity) getActivity()).showSnackBar(getString(R.string.check_connection), view.findViewById(R.id.fragment_display_subject_coordinatorLayout));
                        }

                    } else if (resourceId == R.id.subject_item_display_layout_swipeParent) {
                        final Intent intent = new Intent(getActivity(), MLAViewSubjectActivity.class);
                        intent.putExtra(CommonUtils.EXTRA_IS_TO_ADD, false);
                        intent.putExtra(CommonUtils.EXTRA_EDIT_MODE, false);
                        intent.putExtra(CommonUtils.EXTRA_USER_ADMIN_DATA, item);
                        startActivity(intent);
                    }
                }
            });
            subjectDisplayAdapter.setMode(Attributes.Mode.Single);
            recyclerViewSubjects.setAdapter(subjectDisplayAdapter);
            recyclerViewSubjects.setEmptyView(getView().findViewById(R.id.fragment_display_subject_relEmptyView));
        }

        @Override
        protected List<MLASubjectDetails> doInBackground(Void... params) {

            try {
                Call<List<MLASubjectDetails>> callSubjectData = Api.getClient().getAllSubject();
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

    class MLADeleteSubjectAPI extends AsyncTask<String, Void, String> {
        Context context;

        public MLADeleteSubjectAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            ((MLAHomeActivity) getActivity()).showProgressDialog("Removing Subject...");
        }

        @Override
        protected void onPostExecute(String statusCode) {
            ((MLAHomeActivity) getActivity()).hideProgressDialog();

            if (statusCode != null && statusCode.equals("302")) //the tasks are deleted
            {
                ((MLAHomeActivity) getActivity()).showSnackBar("The subject has been removed.", getView().findViewById(R.id.fragment_display_subject_coordinatorLayout));
                MLAGetAllSubjectDetailsAPI getUserDetails = new MLAGetAllSubjectDetailsAPI(MLASubjectViewFragment.this.getActivity());
                getUserDetails.execute();
            } else {
                ((MLAHomeActivity) getActivity()).showSnackBar(getString(R.string.server_error), getView().findViewById(R.id.fragment_display_subject_coordinatorLayout));
            }
        }

        @Override
        protected String doInBackground(String... params) {
            Call<String> callDelete = Api.getClient().removeSubject(params[0]);
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

}
