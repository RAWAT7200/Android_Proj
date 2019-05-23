package com.paril.mlaclientapp.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import com.daimajia.swipe.util.Attributes;
import com.paril.mlaclientapp.R;
import com.paril.mlaclientapp.model.MLAStudentDetails;
import com.paril.mlaclientapp.sinch.PlaceCallActivity;
import com.paril.mlaclientapp.ui.adapter.MLAStudentAdapter;
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
 * Created by paril on 7/25/2017.
 */

public class CallStudentActivity extends BaseActivity {
    EmptyRecyclerView recyclerViewUsers;
    List<MLAStudentDetails> userDetails = new ArrayList<MLAStudentDetails>();

    ArrayAdapter<String> adapter;
    MLAStudentAdapter userDisplayAdapter;
    public static final String SUBJ_ID = "SUBJ_ID";

    View view;
    String subjId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_mla_studentslist);
        subjId = getIntent().getStringExtra(SUBJ_ID);
        recyclerViewUsers = (EmptyRecyclerView) findViewById(R.id.mla_student_display_recyyclerView);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewUsers.addItemDecoration(new VerticalSpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.divider_list)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setToolbarTitle("Call Student");
        findViewById(R.id.fragment_display_student_fabAddUser).setVisibility(View.GONE);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (CommonUtils.checkInternetConnection(this)) {
            MLAGetEnrollBySubjectAPI mlaGetEnrollBySubjectAPI = new MLAGetEnrollBySubjectAPI(this);
            mlaGetEnrollBySubjectAPI.execute();
        } else {
            showSnackBar(getString(R.string.check_connection), view.findViewById(R.id.fragment_display_admin_coordinatorLayout));
        }

    }

    class MLAGetEnrollBySubjectAPI extends AsyncTask<Void, Void, List<MLAStudentDetails>> {
        Context context;

        public MLAGetEnrollBySubjectAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            showProgressDialog("Getting Student User Data...");

        }

        @Override
        protected void onPostExecute(List<MLAStudentDetails> userDetails) {

            hideProgressDialog();
            List<MLAStudentDetails> listUserDetails = new ArrayList<MLAStudentDetails>();
            if (userDetails != null) {
                listUserDetails = userDetails;


            } else {


                showSnackBar(getString(R.string.server_error), findViewById(R.id.fragment_display_admin_coordinatorLayout));
            }

            userDisplayAdapter = new MLAStudentAdapter(context, listUserDetails, false, new OnItemClickListener<MLAStudentDetails>() {
                @Override
                public void onItemClick(MLAStudentDetails item, int resourceId) {
                    Log.d("OnItemClick", "resource:" + resourceId);
                    if (resourceId == R.id.user_item_display_layout_swipeParent) {
                        final Intent intent = new Intent(CallStudentActivity.this, PlaceCallActivity.class);
                        intent.putExtra(PlaceCallActivity.CALLER_NAME, item.getIdStudent());
                        intent.putExtra(PlaceCallActivity.IS_VIDEO_CALL, getIntent().getBooleanExtra(PlaceCallActivity.IS_VIDEO_CALL
                                , false));
                        startActivity(intent);
                    }

                }
            });
            ((MLAStudentAdapter) userDisplayAdapter).setMode(Attributes.Mode.Single);
            recyclerViewUsers.setAdapter(userDisplayAdapter);
            recyclerViewUsers.setEmptyView(findViewById(R.id.fragment_display_student_relEmptyView));


        }

        @Override
        protected List<MLAStudentDetails> doInBackground(Void... params) {

            try {
                Call<List<MLAStudentDetails>> callAdminUserData = Api.getClient().getEnrollBySub(subjId);
                Response<List<MLAStudentDetails>> responseAdminUser = callAdminUserData.execute();
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

}
