package com.paril.mlaclientapp.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.paril.mlaclientapp.R;
import com.paril.mlaclientapp.model.MLASubjectDetails;

import java.util.List;

/**
 * Created by paril on 7/14/2017.
 */
public class MLAAssignedSubjectAdapter extends RecyclerView.Adapter<MLAAssignedSubjectAdapter.SimpleViewHolder> {
    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        TextView txtDesc, txtTitle;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            txtDesc = (TextView) itemView.findViewById(R.id.assigned_subject_item_display_layout_txtDesc);
            txtTitle = (TextView) itemView.findViewById(R.id.assigned_subject_item_display_layout_txtTitle);
        }

        public void bind(final MLASubjectDetails item) {
            txtDesc.setText(item.getDescription());
            txtTitle.setText(item.getIdSubject() + " " + item.getTitle());

        }
    }

    private Context mContext;
    private List<MLASubjectDetails> mDataset;

    //protected SwipeItemRecyclerMangerImpl mItemManger = new SwipeItemRecyclerMangerImpl(this);

    public MLAAssignedSubjectAdapter(Context context, List<MLASubjectDetails> objects ) {
        this.mContext = context;

        this.mDataset = objects;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.assigned_subject_item_display_layout, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {
        MLASubjectDetails item = mDataset.get(position);
        viewHolder.bind(item);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}

