package com.example.daniel.bankingapp.Administrator.Transaction;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.daniel.bankingapp.Navigation.AdministratorNavigation;
import com.example.daniel.bankingapp.R;

/**
 * Created by Daniel on 11/25/2016.
 */
public class AdminTransaction extends Fragment {

    // declarations
    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 4 ;
    Context context;

    public AdminTransaction(Context context) {

        this.context = context;

    }// end constructor

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.activity_admin_frag_transaction_container,null); // change to admin transaction fragment
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);

        // set adapter for viewPager
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));

        tabLayout.post(new Runnable() {

            @Override
            public void run() {

                tabLayout.setupWithViewPager(viewPager);

            }// end method run

        });

        return view;

    }// end view onCreateView

    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fragmentManager) {

            super(fragmentManager);

        }// end constructor MyAdapter

        // return fragment, according to position
        @Override
        public Fragment getItem(int intPosition)
        {

            switch (intPosition) {

                case 0 : return new AdminWithdraw(context);
                case 1 : return new AdminDeposit(context);
                case 2 : return new AdminBalance(context);
                case 3 : return new AdminTransfer(context);

            }// end switch -> position

            return null;

        }// end method getItem

        @Override
        public int getCount() {

            return int_items;

        }// end method getCount

        // return title of tab according to position
        @Override
        public CharSequence getPageTitle(int intPosition) {

            switch (intPosition) {

                case 0 : return "Withdraw";
                case 1 : return "Deposit";
                case 2 : return "Check Balance";
                case 3 : return "Money Transfer";

            }// end switch -> intPosition

            return null;

        }// ed method getPageTitle

    }// end class MyAdapter

    @Override
    public void onResume() {

        super.onResume();

        // Set title
        ((AdministratorNavigation) getActivity()).setActionBarTitle(getString(R.string.transaction));

    }// end method onResume

}// end class AdminTransaction
