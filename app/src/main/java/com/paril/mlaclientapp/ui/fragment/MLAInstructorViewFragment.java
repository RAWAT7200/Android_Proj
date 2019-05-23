package com.paril.mlaclientapp.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.daimajia.swipe.util.Attributes;
import com.paril.mlaclientapp.R;
import com.paril.mlaclientapp.model.MLAInstructorDetails;
import com.paril.mlaclientapp.ui.activity.MLAHomeActivity;
import com.paril.mlaclientapp.ui.activity.MLAViewInstructorActivity;
import com.paril.mlaclientapp.ui.adapter.MLAInstructorAdapter;
import com.paril.mlaclientapp.ui.adapter.OnItemClickListener;
import com.paril.mlaclientapp.ui.view.EmptyRecyclerView;
import com.paril.mlaclientapp.util.CommonUtils;
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
public class MLAInstructorViewFragment extends Fragment {
    EmptyRecyclerView recyclerViewUsers;
    ArrayAdapter<String> adapter;
    MLAInstructorAdapter mlaUserDisplayAdapter;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mla_instructorlist, container, false);
        recyclerViewUsers = (EmptyRecyclerView) view.findViewById(R.id.mla_instructor_display_recyyclerView);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewUsers.addItemDecoration(new VerticalSpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.divider_list)));

        view.findViewById(R.id.fragment_display_instructor_fabAddUser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(getActivity(), MLAViewInstructorActivity.class);
                intent.putExtra(CommonUtils.EXTRA_IS_TO_ADD, true);
                intent.putExtra(CommonUtils.EXTRA_EDIT_MODE, false);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (CommonUtils.checkInternetConnection(getActivity())) {
            MLAGetAllInstructorAPI getUserDetails = new MLAGetAllInstructorAPI(this.getActivity());
            getUserDetails.execute();
        } else {
            ((MLAHomeActivity) getActivity()).showSnackBar(getString(R.string.check_connection), view.findViewById(R.id.fragment_display_instructor_coordinatorLayout));
        }
    }

    class MLAGetAllInstructorAPI extends AsyncTask<Void, Void, List<MLAInstructorDetails>> {
        Context context;

        public MLAGetAllInstructorAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            ((MLAHomeActivity) getActivity()).showProgressDialog("Getting Instructor User Data...");
        }

        @Override
        protected void onPostExecute(List<MLAInstructorDetails> userDetails) {

            ((MLAHomeActivity) getActivity()).hideProgressDialog();
            List<MLAInstructorDetails> listUserDetails = new ArrayList<MLAInstructorDetails>();
            if (userDetails != null) {
                listUserDetails = userDetails;
            } else {
                ((MLAHomeActivity) getActivity()).showSnackBar(getString(R.string.server_error), getView().findViewById(R.id.fragment_display_instructor_coordinatorLayout));
            }
            mlaUserDisplayAdapter = new MLAInstructorAdapter(context, listUserDetails, new OnItemClickListener<MLAInstructorDetails>() {
                @Override
                public void onItemClick(MLAInstructorDetails item, int resourceId) {
                    Log.d("OnItemClick", "resource:" + resourceId);
                    if (resourceId == R.id.user_item_display_layout_imgEditUser) {
                        final Intent intent = new Intent(getActivity(), MLAViewInstructorActivity.class);
                        intent.putExtra(CommonUtils.EXTRA_IS_TO_ADD, false);
                        intent.putExtra(CommonUtils.EXTRA_EDIT_MODE, true);
                        intent.putExtra(CommonUtils.EXTRA_USER_ADMIN_DATA, item);
                        startActivity(intent);
                    } else if (resourceId == R.id.user_item_display_layout_imgDeleteUser) {
                        if (CommonUtils.checkInternetConnection(getActivity())) {
                            MLADeleteInstructorAPI deleteInstructorTask = new MLADeleteInstructorAPI(MLAInstructorViewFragment.this.getActivity());
                            deleteInstructorTask.execute(item.getIdInstructor());
                        } else {
                            ((MLAHomeActivity) getActivity()).showSnackBar(getString(R.string.check_connection), view.findViewById(R.id.fragment_display_admin_coordinatorLayout));
                        }

                    } else if (resourceId == R.id.user_item_display_layout_swipeParent) {
                        final Intent intent = new Intent(getActivity(), MLAViewInstructorActivity.class);
                        intent.putExtra(CommonUtils.EXTRA_IS_TO_ADD, false);
                        intent.putExtra(CommonUtils.EXTRA_EDIT_MODE, false);
                        intent.putExtra(CommonUtils.EXTRA_USER_ADMIN_DATA, item);
                        startActivity(intent);
                    }

                }
            });
            mlaUserDisplayAdapter.setMode(Attributes.Mode.Single);
            recyclerViewUsers.setAdapter(mlaUserDisplayAdapter);
            recyclerViewUsers.setEmptyView(getView().findViewById(R.id.fragment_display_instructor_relEmptyView));
        }

        @Override
        protected List<MLAInstructorDetails> doInBackground(Void... params) {

            try {
                Call<List<MLAInstructorDetails>> callAdminUserData = Api.getClient().getInstructors();
                Response<List<MLAInstructorDetails>> responseAdminUser = callAdminUserData.execute();
                if (responseAdminUser.isSuccessful() && responseAdminUser.body() != null) {
                    return responseAdminUser.body();
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

    class MLADeleteInstructorAPI extends AsyncTask<String, Void, String> {
        Context context;

        public MLADeleteInstructorAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            ((MLAHomeActivity) getActivity()).showProgressDialog("Removing Instructor...");

        }

        @Override
        protected void onPostExecute(String statusCode) {
            ((MLAHomeActivity) getActivity()).hideProgressDialog();

            if (statusCode != null && statusCode.equals("302")) //the tasks are deleted
            {
                ((MLAHomeActivity) getActivity()).showSnackBar("Instructor User has been removed.", getView().findViewById(R.id.fragment_display_instructor_coordinatorLayout));
                MLAGetAllInstructorAPI getUserDetails = new MLAGetAllInstructorAPI(MLAInstructorViewFragment.this.getActivity());
                getUserDetails.execute();

            } else {
                ((MLAHomeActivity) getActivity()).showSnackBar(getString(R.string.server_error), getView().findViewById(R.id.fragment_display_instructor_coordinatorLayout));
            }
        }

        @Override
        protected String doInBackground(String... params) {
            Call<String> callDelete = Api.getClient().removeInstructor(params[0]);
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
