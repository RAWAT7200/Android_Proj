package com.paril.mlaclientapp.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.paril.mlaclientapp.R;
import com.paril.mlaclientapp.model.MLAAdminDetails;
import com.paril.mlaclientapp.model.MLASubjectDetails;
import com.paril.mlaclientapp.model.MeEncrypt;
import com.paril.mlaclientapp.model.MessageDetail;
import com.paril.mlaclientapp.ui.activity.FinalChat;
import com.paril.mlaclientapp.webservice.Api;
import com.sinch.gson.internal.bind.SqlDateTypeAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Response;


public class ChatMessageActivity extends Fragment {
    ListView listView;
    View view;
    String contacts[] = {"MUKUL", "DEVSHREE", "SHERYL", "ASHKA"};
    List<MessageDetail> msgDetails = new ArrayList<>();

    String frommsg[];
    String msgsub[];
    public String msgdata[];
    String creationdate[];
    String sessiondate[];
    String receiveddate[];

    MeEncrypt ml;

    public class DbHandler extends SQLiteOpenHelper {
        private static final int DB_VERSION = 1;
        private static final String DB_NAME = "msgdb";
        private static final String TABLE_Users = "FINAL";
        private static final String FROM = "frm";
        private static final String MSG_SUB = "msg_sub";
        private static final String MSG_BODY = "msg_body";
        private static final String CREATION_DATE = "creation_date";
        private static final String RECEIVED_DATE = "received_date";
        private static final String SESSION_KEY = "session_key";

        public DbHandler(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String CREATE_TABLE = "CREATE TABLE " + TABLE_Users + "(" + FROM + " TEXT," + MSG_SUB + " TEXT," + MSG_BODY + " TEXT," + CREATION_DATE + " TEXT," + RECEIVED_DATE + " TEXT," + SESSION_KEY + " TEXT" + ")";
            db.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Drop older table if exist
            // db.execSQL("DROP TABLE IF EXISTS " + TABLE_Users);
            onCreate(db);
        }
        public void insert(String frommsg,String msgsub,String msgdata,String creationdate,String receiveddate,String sssiondate) {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues cValues = new ContentValues();
            cValues.put(FROM, frommsg);
            cValues.put(MSG_SUB, msgsub);
            cValues.put(MSG_BODY, msgdata);
            cValues.put(CREATION_DATE, creationdate);
            cValues.put(RECEIVED_DATE,receiveddate);
            cValues.put(SESSION_KEY, sssiondate);

            db.insert(TABLE_Users, null, cValues);

            db.close();
        }
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_message, container, false);

        listView = (ListView) view.findViewById(R.id.contact_list);
/*
        SharedPreferences mSharedPreference= PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        to=(mSharedPreference.getString("userName","SURAJ"));
*/


           DoReceiveMessage ds = new DoReceiveMessage(getActivity());
           ds.execute();

           UpdateReceived up = new UpdateReceived(getActivity());
           up.execute();


        DbHandler dbs=new DbHandler(getActivity());
        SQLiteDatabase db = dbs.getWritableDatabase();
        String query = "SELECT frm,msg_sub,msg_body "+"from "+dbs.TABLE_Users;
        Cursor cursor = db.rawQuery(query,null);
        ArrayList<String> as=new ArrayList<>();
        while (cursor.moveToNext())
        { String fm="FROM :"+cursor.getString(cursor.getColumnIndex(dbs.FROM));
            String msg_s="Subject :"+cursor.getString(cursor.getColumnIndex(dbs.MSG_SUB));
            String msg_b="Message :"+cursor.getString(cursor.getColumnIndex(dbs.MSG_BODY));



           /*

            try{
                KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());

                char password[]={'a','b','c'};
                try {
                    FileInputStream fis = new FileInputStream("MyKeyStore");
                    ks.load(fis, password);
                }catch (Exception e){}

                KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(password);
                // get my private key
                KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry)
                        ks.getEntry("POP", protParam);
                PrivateKey myPrivateKey = pkEntry.getPrivateKey();
                MeEncrypt mss=new MeEncrypt();
                msg_b=mss.RSADecrypt(msg_b,myPrivateKey);
                }
            catch (Exception e){ }
*/

            as.add(""+fm+"\n"+msg_s+"\n"+msg_b);
        }
       cursor.close();
        int n=as.size();

        String resultmsg[] = new String[n];

        for (int i = 0; i < n; i++) {
            resultmsg[i] = as.get(i);
        }

        ArrayAdapter adapter = new ArrayAdapter<>(getActivity(),R.layout.activity_list_item,resultmsg);
        listView.setAdapter(adapter);

        return view;
    }
    class UpdateReceived extends AsyncTask<Void, Void, String> {
        Context context;

        public UpdateReceived(Context cont){this.context=cont;}

        @Override
        protected void onPreExecute() { }

        @Override
        protected void onPostExecute(String statuscode) { }

        @Override
        protected String doInBackground(Void ... params) {

            try {
                Call<String> au=Api.getClient().updateIsReceived(1);
                Response<String> res=au.execute();
            }
            catch (Exception e) { }
            return "";
        }
    }

    public class DoReceiveMessage extends AsyncTask<Void, Void, List<MessageDetail>> {
        Context context;
        public DoReceiveMessage(Context ctx) {
            context = ctx; }

        @Override
        protected void onPreExecute() { }

        @Override
        protected void onPostExecute(List<MessageDetail> ds)
        {
            try {
                msgDetails = ds;
                frommsg = new String[msgDetails.size()];
                msgsub = new String[msgDetails.size()];
                msgdata = new String[msgDetails.size()];
                creationdate = new String[msgDetails.size()];
                receiveddate=new String[msgDetails.size()];
                sessiondate = new String[msgDetails.size()];

                for (int i = 0; i < msgDetails.size(); i++) {

                    frommsg[i] = msgDetails.get(i).fromList;
                    msgsub[i] = msgDetails.get(i).msgSubject;
                    msgdata[i] = msgDetails.get(i).msgBody;
                    creationdate[i] = msgDetails.get(i).creationDate;
                    receiveddate[i]=(new Date()).toString();
                    sessiondate[i] = msgDetails.get(i).sessionKey;
                }

                try{
                    try{
                        File f=new File(getActivity().getCacheDir(),"abcd.ser");
                        FileInputStream file = new FileInputStream(f);
                        ObjectInputStream in = new ObjectInputStream(file);
                        // Method for deserialization of object
                        ml = (MeEncrypt)in.readObject();
                        for (int i = 0; i < msgDetails.size(); i++) {
                            msgdata[i]=ml.RSADecrypt(Base64.decode(msgdata[i],Base64.DEFAULT));
                        }
                        in.close();
                        file.close();
                    }
                    catch (Exception e){}//Toast.makeText(getActivity(),"some error occured",Toast.LENGTH_LONG).show();}

                }
                catch (Exception e){}


                if (msgsub.length > 0) {
                    DbHandler db = new DbHandler(getActivity());
                    for (int i = 0; i < msgsub.length; i++) {
                        db.insert(frommsg[i], msgsub[i], msgdata[i], creationdate[i],receiveddate[i],sessiondate[i]);
                    }
                }

            }
            catch (Exception e) {

                ArrayAdapter adapter = new ArrayAdapter<>(context, R.layout.activity_list_item, new String[]{});
                listView.setAdapter(adapter);

            }
        }

        @Override
        protected List<MessageDetail> doInBackground(Void... params) {
            try{

                Call<List<MessageDetail>> callSubjectData = Api.getClient().receiveMessage();
                Response<List<MessageDetail>> resp = callSubjectData.execute();
                return resp.body();
            }
            catch (Exception e) {
                e.printStackTrace();

            }
            return null;
        }
    }
}




