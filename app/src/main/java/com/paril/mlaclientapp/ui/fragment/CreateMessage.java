package com.paril.mlaclientapp.ui.fragment;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.paril.mlaclientapp.model.ContactChip;
import com.paril.mlaclientapp.model.KeyTable;
import com.paril.mlaclientapp.model.MLAAdminDetails;
import com.paril.mlaclientapp.R;
import com.paril.mlaclientapp.model.MLAInstructorDetails;
import com.paril.mlaclientapp.model.MLARegisterUsers;
import com.paril.mlaclientapp.model.MLAStudentDetails;
import com.paril.mlaclientapp.model.MeEncrypt;
import com.paril.mlaclientapp.ui.activity.MLAHomeActivity;
import com.paril.mlaclientapp.ui.activity.MLAUpdateGradeActivity;
import com.paril.mlaclientapp.webservice.Api;
import com.pchmn.materialchips.ChipsInput;
import com.pchmn.materialchips.R2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.security.auth.x500.X500Principal;

import retrofit2.Call;
import retrofit2.Response;


public class CreateMessage extends Fragment  {
    String namesadmin[];
    List<MLAAdminDetails> md=new ArrayList<>();
    View views;
    Button clickButton;
    EditText t;
    EditText ms;
    EditText msj;
    byte[] enncryptedtext;
    ChipsInput chipsInput;
    String fromlist;
   String tolist;
   String msgbody;String midencrypt;
   String name;
    MeEncrypt mss;
    String decryptedtext;
       @Nullable
       @Override
       public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
           views = inflater.inflate(R.layout.activity_create_message, container, false);
           chipsInput = (ChipsInput) views.findViewById(R.id.chips_input);
           MLAGetAllUsersDetailsAPI getUserDetails = new MLAGetAllUsersDetailsAPI(getActivity());
           getUserDetails.execute();

           try{
               mss=new MeEncrypt();
               //Saving of object in a file
             try {
                 File f=new File(getActivity().getCacheDir(),"abcd.ser");
                 FileOutputStream file = new FileOutputStream(f);
                 ObjectOutputStream out = new ObjectOutputStream(file);
                 out.writeObject(mss);
                 out.close();
                 file.close();
             }
             catch (Exception e){}
           }
           catch (Exception e){}

        SendKey S=new SendKey(getActivity());
           S.execute();

           msj = (EditText) views.findViewById(R.id.sub);
           t = (EditText) views.findViewById(R.id.from);
           ms = (EditText) views.findViewById(R.id.msg);
           msgbody = ms.getText().toString();



           SharedPreferences mSharedPreference = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
           String value = (mSharedPreference.getString("userName", "SURAJ"));
           t.setText(value);

           clickButton = (Button) views.findViewById(R.id.sd);
           clickButton.setOnClickListener(new View.OnClickListener() {

               @Override
               public void onClick(View v) {

                   try {
                       /*GetKey gk = new GetKey(getActivity());
                       gk.execute();
                       */
                   } catch (Exception e) { }

                   try {
                       msgbody=ms.getText().toString();
                       enncryptedtext=mss.RSAEncrypt(msgbody);
                       midencrypt=Base64.encodeToString(enncryptedtext,Base64.DEFAULT);
                       msgbody=midencrypt;
                       decryptedtext=mss.RSADecrypt(enncryptedtext);
                       Toast.makeText(getActivity(),"after Encryption:"+midencrypt+"Final decrypted text :"+decryptedtext,Toast.LENGTH_LONG).show();
                   }catch (Exception e){}

                   List<ContactChip> contactselected = (List<ContactChip>) chipsInput.getSelectedChipList();
                   name = contactselected.get(0).getEmail();

                   DoMessage ds = new DoMessage(getActivity());
                   ds.execute();


               }
           });


           return views;
       }

       class GetKey extends AsyncTask<Void, Void, List<KeyTable>> {
           Context context;

           public GetKey(Context ctx) {
               context = ctx;
           }

           @Override
           protected void onPreExecute() {
           }

           @Override
           protected void onPostExecute(List<KeyTable> res) {


           }

           @Override
           protected List<KeyTable> doInBackground(Void... params) {

               try {
                   Call<List<KeyTable>> call = Api.getClient().getKeyWithUserId("POP");
                   Response<List<KeyTable>> res = call.execute();
                   return res.body();
               } catch (Exception e) {

               }
               return null;
           }

       }

       class SendKey extends AsyncTask<Void, Void, String> {
           Context context;

           public SendKey(Context ctx) {
               context = ctx;
           }

           @Override
           protected void onPreExecute() {
           }

           @Override
           protected void onPostExecute(String res) {
               if (res == "202 ok") {
               }
           }
           @Override
           protected String doInBackground(Void... params) {

               try {
                    byte []b=mss.publicKey.getEncoded();
                   Call<String> res = Api.getClient().insertKey("PULKIT",b);
                   Response<String> ress = res.execute();
               } catch (Exception e) { }
               return "";
           }
       }

       class MLAGetAllUsersDetailsAPI extends AsyncTask<Void, Void, List<ContactChip>> {
           Context context;

           public MLAGetAllUsersDetailsAPI(Context ctx) {
               context = ctx;
           }

           @Override
           protected void onPreExecute() {

           }

           @Override
           protected void onPostExecute(List<ContactChip> userDetails) {

               //check if the call to api passed
               if (userDetails != null) {
                   chipsInput.setFilterableList(userDetails);
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
                           ContactChip contactChip = new ContactChip(adminUserDetail.getIdAdmin(), adminUserDetail.getEmailId(), adminUserDetail.getFirstName() + " " + adminUserDetail.getLastName(), "Admin");
                           listContactChip.add(contactChip);
                       }
                   }

                   Call<List<MLAInstructorDetails>> callInstUserData = Api.getClient().getInstructors();
                   Response<List<MLAInstructorDetails>> responseInstUser = callInstUserData.execute();
                   if (responseInstUser.isSuccessful() && responseInstUser.body() != null) {
                       for (MLAInstructorDetails instUserDetail : responseInstUser.body()
                       ) {
                           ContactChip contactChip = new ContactChip(instUserDetail.getIdInstructor(), instUserDetail.getEmailId(), instUserDetail.getFirstName() + " " + instUserDetail.getLastName(), "Instructor");
                           listContactChip.add(contactChip);
                       }
                   }

                   Call<List<MLAStudentDetails>> callStudentUserData = Api.getClient().getStudents();
                   Response<List<MLAStudentDetails>> responseStudentUser = callStudentUserData.execute();
                   if (responseStudentUser.isSuccessful() && responseStudentUser.body() != null) {
                       for (MLAStudentDetails studentUserDetail : responseStudentUser.body()
                       ) {
                           ContactChip contactChip = new ContactChip(studentUserDetail.getIdStudent(), studentUserDetail.getEmailId(), studentUserDetail.getFirstName() + " " + studentUserDetail.getLastName(), "Student");
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

       class DoMessage extends AsyncTask<Void, Void, String> {
           Context context;
           String msgsubject;
           String creationdate;
           String sessionkey;
           int isreceived;

           public DoMessage(Context ctx) {
               context = ctx;
               isreceived = 0;
           }

           @Override
           protected void onPreExecute() {
               msgsubject = msj.getText().toString();
               fromlist = t.getText().toString();
               //tolist=e.getText().toString();
              msgbody=ms.getText().toString();
               //msgbody=midencrypt;
               tolist = name;
               creationdate = (new Date()).toString();
               sessionkey = (new Date()).toString();
           }

           @Override
           protected void onPostExecute(String statusCode) {

               Toast.makeText(getActivity(),"The message is sent:",Toast.LENGTH_LONG).show();
           }

           @Override
           protected String doInBackground(Void... params) {

               try {
                   Call<String> callGradTas = Api.getClient().sendMessage(fromlist,tolist, msgsubject, msgbody, creationdate, sessionkey, isreceived);
                   Response<String> resGradTas = callGradTas.execute();


               } catch (Exception e) {

                   e.printStackTrace();
               }
               return "";
           }
       }
   }


