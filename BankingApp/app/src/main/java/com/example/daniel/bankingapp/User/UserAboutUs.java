package com.example.daniel.bankingapp.User;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.daniel.bankingapp.Navigation.UserNavigation;
import com.example.daniel.bankingapp.R;

/**
 * Created by Daniel on 11/20/2016.
 */
public class UserAboutUs extends Fragment {

    public UserAboutUs() {

        // Required empty public constructor

    }// end constructor

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // return inflated layout for this fragment
        return inflater.inflate(R.layout.activity_user_frag_about, container, false);

    }// end onCreateView

    @Override
    public void onResume() {

        super.onResume();

        // Set title
        ((UserNavigation) getActivity()).setActionBarTitle(getString(R.string.aboutUs));

    }// end method onResume

}// end class UserAboutUs