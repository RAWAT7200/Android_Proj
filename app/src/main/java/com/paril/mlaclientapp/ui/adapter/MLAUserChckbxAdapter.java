package com.paril.mlaclientapp.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.paril.mlaclientapp.R;
import com.paril.mlaclientapp.model.MLAUserWithCheckbox;

import java.util.List;

/**
 * Created by paril on 7/16/2017.
 */
public class MLAUserChckbxAdapter extends BaseAdapter {
    List<MLAUserWithCheckbox> list;
    Context mContext;

    public MLAUserChckbxAdapter(Context context, List<MLAUserWithCheckbox> list) {
        this.list = list;
        this.mContext = context;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public Object getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;
        rowView = convertView;
        MLADataAdapter dataAdapter;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.rowlayout_mla_chckbxusers, parent, false);
            dataAdapter = new MLADataAdapter();
            dataAdapter.txtName = (TextView) rowView.findViewById(R.id.mla_chckbxusers_txtname);
            dataAdapter.txtEmail = (TextView) rowView.findViewById(R.id.mla_chckbxusers_txtemail);
            dataAdapter.chckBox = (CheckBox) rowView.findViewById(R.id.mla_chckbxusers_checkBx);
            rowView.setTag(dataAdapter);
            rowView.setTag(R.id.mla_chckbxusers_txtname, dataAdapter.txtName);
            rowView.setTag(R.id.mla_chckbxusers_txtemail, dataAdapter.txtEmail);
            rowView.setTag(R.id.mla_chckbxusers_checkBx, dataAdapter.chckBox);

            dataAdapter.chckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int getPosition = (Integer) buttonView.getTag();
                    MLAUserWithCheckbox userDisplayCheckbxProvider;
                    userDisplayCheckbxProvider =  list.get(getPosition);
                    userDisplayCheckbxProvider.setCheck(buttonView.isChecked());
                }
            });

        } else {
            dataAdapter = (MLADataAdapter) rowView.getTag();
        }
        dataAdapter.chckBox.setTag(position);

        MLAUserWithCheckbox userDisplayCheckbxProvider;
        userDisplayCheckbxProvider = (MLAUserWithCheckbox) this.getItem(position);
        dataAdapter.txtName.setText(userDisplayCheckbxProvider.getUserFLname());
        dataAdapter.txtEmail.setText(userDisplayCheckbxProvider.getEmail());

        dataAdapter.chckBox.setChecked(userDisplayCheckbxProvider.getCheck());

        return rowView;
    }

    static class MLADataAdapter {
        TextView txtName;
        TextView txtEmail;
        CheckBox chckBox;
    }
}