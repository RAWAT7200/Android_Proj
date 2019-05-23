package com.paril.mlaclientapp.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.paril.mlaclientapp.R;
import com.paril.mlaclientapp.model.ContactChip;
import com.paril.mlaclientapp.model.MLAAdminDetails;
import com.paril.mlaclientapp.model.MLAInstructorDetails;
import com.paril.mlaclientapp.model.MLAStudentDetails;
import com.paril.mlaclientapp.ui.activity.MLAHomeActivity;
import com.paril.mlaclientapp.webservice.Api;
import com.pchmn.materialchips.ChipsInput;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by paril on 7/16/2017.
 */
public class MLANewMessageFragment extends Fragment {

    View view;
    ChipsInput chipsInput;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_send, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_send) {
            List<ContactChip> contactsSelected = (List<ContactChip>) chipsInput.getSelectedChipList();

            if (contactsSelected == null || contactsSelected.size() == 0) {
                ((MLAHomeActivity) getActivity()).showSnackBar("Please select a recipient to send a message.", getView().findViewById(R.id.fragment_new_message_coordinatorLayout));
            } else {
                String[] emaiiArray = new String[contactsSelected.size()];
                for (int i = 0; i < contactsSelected.size(); i++) {
                    emaiiArray[i] = contactsSelected.get(i).getEmail();
                }
                final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("text/plain");
                final PackageManager pm = getActivity().getPackageManager();
                final List<ResolveInfo> matches = pm.queryIntentActivities(intent, 0);
                ResolveInfo best = null;
                for (final ResolveInfo info : matches)
                    if (info.activityInfo.packageName.endsWith(".gm") ||
                            info.activityInfo.name.toLowerCase().contains("gmail")) best = info;
                if (best != null)
                    intent.setClassName(best.activityInfo.packageName, best.activityInfo.name);

                intent.putExtra(Intent.EXTRA_EMAIL, emaiiArray);
                startActivity(intent);
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mla_newmessage, container, false);
        setHasOptionsMenu(true);
        chipsInput = (ChipsInput) view.findViewById(R.id.chips_input);
        MLAGetAllUsersDetailsAPI getUserDetails = new MLAGetAllUsersDetailsAPI(getActivity());
        getUserDetails.execute();
        return view;
    }

    class MLAGetAllUsersDetailsAPI extends AsyncTask<Void, Void, List<ContactChip>> {
        Context context;

        public MLAGetAllUsersDetailsAPI(Context ctx) {
            context = ctx;
        }

        @Override
        protected void onPreExecute() {
            ((MLAHomeActivity) getActivity()).showProgressDialog("Getting User Details...");
        }

        @Override
        protected void onPostExecute(List<ContactChip> userDetails) {
            ((MLAHomeActivity) getActivity()).hideProgressDialog();
            //check if the call to api passed
            if (userDetails != null) {
                chipsInput.setFilterableList(userDetails);
            } else {
                ((MLAHomeActivity) getActivity()).showSnackBar(getString(R.string.server_error), getView().findViewById(R.id.fragment_new_message_coordinatorLayout));
            }
        }

        @Override
        protected List<ContactChip> doInBackground(Void... params) {
            List<ContactChip> listContactChip = new ArrayList<ContactChip>();

            try {
                Call<List<MLAAdminDetails>> callAdminUserData = Api.getClient().getAdminUsers();
                Response<List<MLAAdminDetails>> responseAdminUser = callAdminUserData.execute();
                if (responseAdminUser.isSuccessful() && responseAdminUser.body() != null) {
                    for (MLAAdminDetails adminUserDetail : responseAdminUser.body()
                            ) {
                        ContactChip contactChip = new ContactChip(adminUserDetail.getIdAdmin(), adminUserDetail.getEmailId(), adminUserDetail.getFirstName() + " " +adminUserDetail.getLastName(), "Admin");
                        listContactChip.add(contactChip);
                    }
                }

                Call<List<MLAInstructorDetails>> callInstUserData = Api.getClient().getInstructors();
                Response<List<MLAInstructorDetails>> responseInstUser = callInstUserData.execute();
                if (responseInstUser.isSuccessful() && responseInstUser.body() != null) {
                    for (MLAInstructorDetails instUserDetail : responseInstUser.body()
                            ) {
                        ContactChip contactChip = new ContactChip(instUserDetail.getIdInstructor(), instUserDetail.getEmailId(), instUserDetail.getFirstName() +" " + instUserDetail.getLastName(), "Instructor");
                        listContactChip.add(contactChip);
                    }
                }

                Call<List<MLAStudentDetails>> callStudentUserData = Api.getClient().getStudents();
                Response<List<MLAStudentDetails>> responseStudentUser = callStudentUserData.execute();
                if (responseStudentUser.isSuccessful() && responseStudentUser.body() != null) {
                    for (MLAStudentDetails studentUserDetail : responseStudentUser.body()
                            ) {
                        ContactChip contactChip = new ContactChip(studentUserDetail.getIdStudent(), studentUserDetail.getEmailId(), studentUserDetail.getFirstName()+" " + studentUserDetail.getLastName(), "Student");
                        listContactChip.add(contactChip);
                    }
                }
                return listContactChip;
            } catch (MalformedURLException e) {
                return null;

            } catch (IOException e) {
                return null;
            }
        }

    }

}

