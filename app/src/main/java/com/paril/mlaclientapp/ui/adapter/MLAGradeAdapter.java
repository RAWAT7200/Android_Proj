package com.paril.mlaclientapp.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.paril.mlaclientapp.R;
import com.paril.mlaclientapp.model.MLAGradeTask;
import com.paril.mlaclientapp.ui.view.CustomSwipeLayout;

import java.util.List;

/**
 * Created by paril on 7/14/2017.
 */
public class MLAGradeAdapter extends RecyclerSwipeAdapter<MLAGradeAdapter.SimpleViewHolder> {

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        CustomSwipeLayout swipeLayout;
        TextView txtName, txtGrade;
        ImageView imgEdit;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            swipeLayout = (CustomSwipeLayout) itemView.findViewById(R.id.row_display_grade_swipeParent);
            txtName = (TextView) itemView.findViewById(R.id.row_display_grade_txtName);
            txtGrade = (TextView) itemView.findViewById(R.id.row_display_grade_txtGrade);
            imgEdit = (ImageView) itemView.findViewById(R.id.row_display_grade_imgEditUser);

        }

        public void bind(final MLAGradeTask item, final OnItemClickListener listener) {
            swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

            swipeLayout.addSwipeListener(new SimpleSwipeListener() {
                @Override
                public void onOpen(SwipeLayout layout) {
                }
            });

            imgEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    swipeLayout.close();

                    listener.onItemClick(item, R.id.row_display_grade_imgEditUser);
                }
            });
            swipeLayout.setOnClickItemListener(new CustomSwipeLayout.OnClickItemListener() {
                @Override
                public void onClick(View view) {

                    listener.onItemClick(item, R.id.row_display_grade_swipeParent);
                }
            });
            txtName.setText(item.getStudent_id());

            if (TextUtils.isEmpty(item.getInstr_grade())) {
                txtGrade.setText("Not Graded");
            } else {
                txtGrade.setText(item.getInstr_grade());
            }
        }
    }

    private Context mContext;
    private List<MLAGradeTask> mDataset;

    OnItemClickListener<MLAGradeTask> listener;

    //protected SwipeItemRecyclerMangerImpl mItemManger = new SwipeItemRecyclerMangerImpl(this);

    public MLAGradeAdapter(Context context, List<MLAGradeTask> objects, OnItemClickListener<MLAGradeTask> onItemClickListenerener) {
        this.mContext = context;

        this.mDataset = objects;
        this.listener = onItemClickListenerener;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowlayout_mla_gradedisplay, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {
        MLAGradeTask item = mDataset.get(position);
        viewHolder.bind(item, listener);
        mItemManger.bindView(viewHolder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.row_display_grade_swipeParent;
    }
}