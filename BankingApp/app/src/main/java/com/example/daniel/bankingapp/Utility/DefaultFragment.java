package com.example.daniel.bankingapp.Utility;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.daniel.bankingapp.R;

/**
 * Created by Daniel on 11/30/2016.
 */
public class DefaultFragment extends Fragment {

    public DefaultFragment() {

        // Required empty public constructor

    }// end constructor

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // return inflated layout for this fragment
        return inflater.inflate(R.layout.generic_default_frag, container, false);

    }// end onCreateView

}// end class DefaultFragment
