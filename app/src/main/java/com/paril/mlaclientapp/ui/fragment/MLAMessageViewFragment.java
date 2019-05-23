package com.paril.mlaclientapp.ui.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by paril on 7/16/2017.
 */
public class MLAMessageViewFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //This code will launch gmail app which is installed on the client
        Intent gmailIntent = MLAMessageViewFragment.this.getActivity().getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
        startActivity(gmailIntent);

        return super.onCreateView(inflater, container, savedInstanceState);


    }
}
