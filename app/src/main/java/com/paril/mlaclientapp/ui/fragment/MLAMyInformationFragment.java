package com.paril.mlaclientapp.ui.fragment;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.paril.mlaclientapp.R;
import com.paril.mlaclientapp.model.MLAAdminDetails;
import com.paril.mlaclientapp.model.MLAInstructorDetails;
import com.paril.mlaclientapp.model.MLAStudentDetails;
import com.paril.mlaclientapp.webservice.Api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by paril on 7/12/2017.
 */
public class MLAMyInformationFragment extends Fragment {

//    TextView txtUserId;
    TextView txtUserName;
    TextView txtFirstName;
    TextView txtLastName;
    TextView txtEmailId;
    TextView txtTelephone;
    TextView txtAliasMailId;
    TextView txtAddress;
//    TextView txtHangoutId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mla_myinformation,container,false);

        //txtUserId = (TextView) view.findViewById(R.id.mla_myinfo_txtUserId);
        txtUserName = (TextView) view.findViewById(R.id.mla_myinfo_txtUserName);
        txtFirstName= (TextView) view.findViewById(R.id.mla_myinfo_txtFirstName);
        txtLastName=(TextView) view.findViewById(R.id.mla_myinfo_txtLastName);
        txtEmailId = (TextView) view.findViewById(R.id.mla_myinfo_txtEmailId);
        txtTelephone = (TextView) view.findViewById(R.id.mla_myinfo_txtTelephone);
        txtAliasMailId = (TextView) view.findViewById(R.id.mla_myinfo_txtAliasMailId);
        txtAddress = (TextView) view.findViewById(R.id.mla_myinfo_txtAddress);
        //txtHangoutId = (TextView) view.findViewById(R.id.mla_myinfo_txtHangoutId);

        MLAGetUserInformationAPI getUserDetails=new MLAGetUserInformationAPI();
        Bundle bundle = getActivity().getIntent().getExtras();
        getUserDetails.execute(bundle.get("userName").toString(),bundle.get("userType").toString());
        return view;

    }
    //GetStudentByUserName
    //GetAdminByUserName  Admin
    class MLAGetUserInformationAPI extends AsyncTask<String,Void,Void>
    {
        MLAAdminDetails adminUserDetails =new MLAAdminDetails();
        MLAInstructorDetails instructorDetails=new MLAInstructorDetails();
        MLAStudentDetails studentDetails=new MLAStudentDetails();
        String userType;
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(Void result) {
            if(userType != null  && userType.equals("admin") ) {

                //txtUserId.setText(adminUserDetails.userId+"");
                txtUserName.setText(adminUserDetails.getIdAdmin());
                txtFirstName.setText(adminUserDetails.firstName);
                txtLastName.setText(adminUserDetails.lastName);
                txtEmailId.setText(adminUserDetails.emailId);
                txtTelephone.setText(adminUserDetails.telephone);
                txtAliasMailId.setText(adminUserDetails.aliasMailId);
                txtAddress.setText(adminUserDetails.address);
                //txtHangoutId.setText(adminUserDetails.skypeId);
            }else if(userType != null  && userType.equals("student") ) {

                //txtUserId.setText(studentDetails.userId+"");
                txtUserName.setText(studentDetails.getIdStudent());
                txtFirstName.setText(studentDetails.firstName);
                txtLastName.setText(studentDetails.lastName);
                txtEmailId.setText(studentDetails.emailId);
                txtTelephone.setText(studentDetails.telephone);
                txtAliasMailId.setText(studentDetails.aliasMailId);
                txtAddress.setText(studentDetails.address);
                //txtHangoutId.setText(studentDetails.skypeId);
            }else if(userType != null  && userType.equals("instructor") ) {

                //txtUserId.setText(instructorDetails.userId+"");
                txtUserName.setText(instructorDetails.getIdInstructor());
                txtFirstName.setText(instructorDetails.firstName);
                txtLastName.setText(instructorDetails.lastName);
                txtEmailId.setText(instructorDetails.emailId);
                txtTelephone.setText(instructorDetails.telephone);
                txtAliasMailId.setText(instructorDetails.aliasMailId);
                txtAddress.setText(instructorDetails.address);
                //txtHangoutId.setText(instructorDetails.skypeId);
            }
        }

        @Override
        protected Void doInBackground(String... params) {
            userType=params[1];
            if(params[1].equals("admin")) {
                try {
                    Call<List<MLAAdminDetails>> callAdminData = Api.getClient().getAdminInfo(params[0]);
                    Response<List<MLAAdminDetails>> responseAdminData = callAdminData.execute();
                    if (responseAdminData.isSuccessful() && responseAdminData.body() != null&&responseAdminData.body() .size()>0) {
                        adminUserDetails= responseAdminData.body().get(0);
                    }

                } catch (MalformedURLException e) {
                    return null;

                } catch (IOException e) {
                    return null;
                }
            }
            else if(params[1].equals("student"))
            {
                try {
                    Call<List<MLAStudentDetails>> callStudentData = Api.getClient().getStudentInfo(params[0]);
                    Response<List<MLAStudentDetails>> responseStudentData = callStudentData.execute();
                    if (responseStudentData.isSuccessful() && responseStudentData.body() != null&&responseStudentData.body() .size()>0) {
                        studentDetails= responseStudentData.body().get(0);
                    }

                } catch (MalformedURLException e) {
                    return null;

                } catch (IOException e) {
                    return null;
                }
            }
            else if(params[1].equals("instructor"))
            {
                try {
                    Call<List<MLAInstructorDetails>> callInstData = Api.getClient().getInstInfo(params[0]);
                    Response<List<MLAInstructorDetails>> responseInstData = callInstData.execute();
                    if (responseInstData.isSuccessful() && responseInstData.body() != null&&responseInstData.body() .size()>0) {
                        instructorDetails= responseInstData.body().get(0);
                    }

                } catch (MalformedURLException e) {
                    return null;

                } catch (IOException e) {
                    return null;
                }
            }

            return null;
        }
    }
}
