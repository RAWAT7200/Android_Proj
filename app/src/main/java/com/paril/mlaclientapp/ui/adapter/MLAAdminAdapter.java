package com.paril.mlaclientapp.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.paril.mlaclientapp.R;
import com.paril.mlaclientapp.model.MLAAdminDetails;
import com.paril.mlaclientapp.ui.view.CustomSwipeLayout;

import java.util.List;

/**
 * Created by paril on 7/14/2017.
 */
public class MLAAdminAdapter extends RecyclerSwipeAdapter<MLAAdminAdapter.SimpleViewHolder> {

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        CustomSwipeLayout swipeLayout;
        TextView txtUserName, txtName, txtEmailId;
        ImageView imgDelete, imgEdit;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            swipeLayout = (CustomSwipeLayout) itemView.findViewById(R.id.user_item_display_layout_swipeParent);
            txtName = (TextView) itemView.findViewById(R.id.user_item_display_layout_txtName);
            txtUserName = (TextView) itemView.findViewById(R.id.user_item_display_layout_txtUserName);
            txtEmailId = (TextView) itemView.findViewById(R.id.user_item_display_layout_txtEmail);
            imgDelete = (ImageView) itemView.findViewById(R.id.user_item_display_layout_imgDeleteUser);
            imgEdit = (ImageView) itemView.findViewById(R.id.user_item_display_layout_imgEditUser);
        }

        public void bind(final MLAAdminDetails item, final OnItemClickListener listener) {
            swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

            swipeLayout.addSwipeListener(new SimpleSwipeListener() {
                @Override
                public void onOpen(SwipeLayout layout) {
                }
            });
            imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    swipeLayout.close();
                    listener.onItemClick(item,R.id.user_item_display_layout_imgDeleteUser);
                }
            });


            imgEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    swipeLayout.close();

                    listener.onItemClick(item,R.id.user_item_display_layout_imgEditUser);
                }
            });
            swipeLayout.setOnClickItemListener(new CustomSwipeLayout.OnClickItemListener() {
                @Override
                public void onClick(View view) {

                    listener.onItemClick(item,R.id.user_item_display_layout_swipeParent);
                }
            });
            txtName.setText(item.getFirstName() + " " + item.getLastName());

            txtUserName.setText(item.getIdAdmin());

            txtEmailId.setText(item.getEmailId());
        }
    }

    private Context mContext;
    private List<MLAAdminDetails> mDataset;

    OnItemClickListener<MLAAdminDetails> listener;

    //protected SwipeItemRecyclerMangerImpl mItemManger = new SwipeItemRecyclerMangerImpl(this);

    public MLAAdminAdapter(Context context, List<MLAAdminDetails> objects, OnItemClickListener<MLAAdminDetails> onItemClickListenerener) {
        this.mContext = context;

        this.mDataset = objects;
        this.listener = onItemClickListenerener;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowlayout_mla_displayusers, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {
        MLAAdminDetails item = mDataset.get(position);
        viewHolder.bind(item, listener);
        mItemManger.bindView(viewHolder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.user_item_display_layout_swipeParent;
    }
}