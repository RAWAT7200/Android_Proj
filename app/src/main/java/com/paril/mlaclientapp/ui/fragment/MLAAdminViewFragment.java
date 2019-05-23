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

import com.daimajia.swipe.util.Attributes;
import com.paril.mlaclientapp.R;
import com.paril.mlaclientapp.model.MLAAdminDetails;
import com.paril.mlaclientapp.ui.activity.MLAHomeActivity;
import com.paril.mlaclientapp.ui.activity.MLAViewAdminActivity;
import com.paril.mlaclientapp.ui.adapter.MLAAdminAdapter;
import com.paril.mlaclientapp.ui.adapter.OnItemClickListener;
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
public class MLAAdminViewFragment extends Fragment {
    com.paril.mlaclientapp.ui.view.EmptyRecyclerView recyclerViewUsers;
    MLAAdminAdapter mlaUserDisplayAdapter;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mla_adminlist, container, false);
        recyclerViewUsers = (com.paril.mlaclientapp.ui.view.EmptyRecyclerView) view.findViewById(R.id.mla_admin_display_recyyclerView);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewUsers.addItemDecoration(new VerticalSpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.divider_list)));

        view.findViewById(R.id.fragment_display_admin_fabAddUser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(getActivity(), MLAViewAdminActivity.class);
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
            MLAGetAllAdminAPI getUserDetails = new MLAGetAllAdminAPI(this.getActivity());
            getUserDetails.execute();
        } else {
            ((MLAHomeActivity) getActivity()).showSnackBar(getString(R.string.check_connection), view.findViewById(R.id.fragment_display_admin_coordinatorLayout));
        }
    }

    class MLAGetAllAdminAPI extends AsyncTask<Void, Void, List<MLAAdminDetails>> {
        Context context;

        public MLAGetAllAdminAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            ((MLAHomeActivity) getActivity()).showProgressDialog("Fetching all Admin...");
        }

        @Override
        protected void onPostExecute(List<MLAAdminDetails> userDetails) {

            ((MLAHomeActivity) getActivity()).hideProgressDialog();
            List<MLAAdminDetails> listUserDetails = new ArrayList<MLAAdminDetails>();
            if (userDetails != null) {
                listUserDetails = userDetails;
            } else {

                ((MLAHomeActivity) getActivity()).showSnackBar(getString(R.string.server_error), getView().findViewById(R.id.fragment_display_admin_coordinatorLayout));
            }

            mlaUserDisplayAdapter = new MLAAdminAdapter(context, listUserDetails, new OnItemClickListener<MLAAdminDetails>() {
                @Override
                public void onItemClick(MLAAdminDetails item, int resourceId) {
                    Log.d("OnItemClick", "resource:" + resourceId);
                    if (resourceId == R.id.user_item_display_layout_imgEditUser) {
                        final Intent intent = new Intent(getActivity(), MLAViewAdminActivity.class);
                        intent.putExtra(CommonUtils.EXTRA_IS_TO_ADD, false);
                        intent.putExtra(CommonUtils.EXTRA_EDIT_MODE, true);
                        intent.putExtra(CommonUtils.EXTRA_USER_ADMIN_DATA, item);
                        startActivity(intent);
                    } else if (resourceId == R.id.user_item_display_layout_imgDeleteUser) {
                        if (CommonUtils.checkInternetConnection(getActivity())) {
                            MLAPostAdminRmvAPI postAdminRmvAPI = new MLAPostAdminRmvAPI(MLAAdminViewFragment.this.getActivity());
                            postAdminRmvAPI.execute(item.getIdAdmin());
                        } else {
                            ((MLAHomeActivity) getActivity()).showSnackBar(getString(R.string.check_connection), view.findViewById(R.id.fragment_display_admin_coordinatorLayout));
                        }

                    } else if (resourceId == R.id.user_item_display_layout_swipeParent) {
                        final Intent intent = new Intent(getActivity(), MLAViewAdminActivity.class);
                        intent.putExtra(CommonUtils.EXTRA_IS_TO_ADD, false);
                        intent.putExtra(CommonUtils.EXTRA_EDIT_MODE, false);

                        intent.putExtra(CommonUtils.EXTRA_USER_ADMIN_DATA, item);
                        startActivity(intent);
                    }

                }
            });
            mlaUserDisplayAdapter.setMode(Attributes.Mode.Single);
            recyclerViewUsers.setAdapter(mlaUserDisplayAdapter);
            recyclerViewUsers.setEmptyView(getView().findViewById(R.id.fragment_display_admin_relEmptyView));
        }

        @Override
        protected List<MLAAdminDetails> doInBackground(Void... params) {

            try {
                Call<List<MLAAdminDetails>> callAdminUserData = Api.getClient().getAdminUsers();
                Response<List<MLAAdminDetails>> responseAdminUser = callAdminUserData.execute();
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

    class MLAPostAdminRmvAPI extends AsyncTask<String, Void, String> {
        Context context;

        public MLAPostAdminRmvAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            ((MLAHomeActivity) getActivity()).showProgressDialog("Removing Admin...");
        }

        @Override
        protected void onPostExecute(String statusCode) {
            ((MLAHomeActivity) getActivity()).hideProgressDialog();

            if (statusCode != null && statusCode.equals("302")) //the tasks are deleted
            {
                ((MLAHomeActivity) getActivity()).showSnackBar("The admin has been removed.", getView().findViewById(R.id.fragment_display_admin_coordinatorLayout));
                MLAGetAllAdminAPI getUserDetails = new MLAGetAllAdminAPI(MLAAdminViewFragment.this.getActivity());
                getUserDetails.execute();

            } else {
                ((MLAHomeActivity) getActivity()).showSnackBar(getString(R.string.server_error), getView().findViewById(R.id.fragment_display_admin_coordinatorLayout));

            }
        }

        @Override
        protected String doInBackground(String... params) {
            Call<String> callDelete = Api.getClient().removeAdmin(params[0]);
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
