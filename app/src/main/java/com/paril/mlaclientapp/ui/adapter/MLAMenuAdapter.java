package com.paril.mlaclientapp.ui.adapter;

/**
 * Created by paril on 7/11/2017.
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.paril.mlaclientapp.model.MLAListModel;
import com.paril.mlaclientapp.R;

import java.util.HashMap;
import java.util.List;

public class MLAMenuAdapter extends BaseExpandableListAdapter {
    private Context ctx;
    private HashMap<String,List<String>> hashMap;
    private List<String> list;


    public MLAMenuAdapter(Context ctx, HashMap navDrawerHashMap, List navDrawerParentList)
    {
        this.ctx = ctx;
        this.hashMap = navDrawerHashMap;
        this.list = navDrawerParentList;
    }
    @Override
    public int getGroupCount() {
        return  list.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        return hashMap.get(list.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return list.get(groupPosition) ;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return hashMap.get(list.get(groupPosition)).get(childPosition);

    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String groupTitle = (String) getGroup(groupPosition);
        if(convertView == null)
        {
            LayoutInflater inflator = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflator.inflate(R.layout.drawer_mla_primary,parent,false);
        }
        TextView txtPrimary = (TextView) convertView.findViewById(R.id.mla_txtParentTitle);
        txtPrimary.setText(groupTitle);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        MLAListModel secondaryItem = (MLAListModel) getChild(groupPosition,childPosition);
        String secondaryTitle = secondaryItem.getTxtView();

        if(convertView == null)
        {
            LayoutInflater inflator = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflator.inflate(R.layout.drawer_mla_secondary, parent,false);
        }

        TextView txtSecondary = (TextView) convertView.findViewById(R.id.mla_txtSecondaryTitle);
        txtSecondary.setText(secondaryTitle);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
