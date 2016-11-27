package com.example.daniel.bankingapp.User;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.daniel.bankingapp.Database.DatabaseHelper;
import com.example.daniel.bankingapp.Navigation.UserNavigation;
import com.example.daniel.bankingapp.R;
import com.example.daniel.bankingapp.Utility.SessionManager;

/**
 * Created by Daniel on 11/20/2016.
 */
public class UserBalance extends Fragment {

    // declarations
    Context context;

    // sessionManager
    SessionManager sessionManager;

    // textViews -> balance form
    TextView textViewBalance;
    TextView textViewAccountNo;

    public UserBalance(Context context) {

        this.context = context;

    }// end constructor

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_user_frag_balance, container, false);

        // new instances of sessionManager & bankAccount
        sessionManager = new SessionManager();

        // assign textViews to variables
        textViewBalance = (TextView) view.findViewById(R.id.textBalanceRemaining);
        textViewAccountNo = (TextView) view.findViewById(R.id.textBalanceAccountNo);

        // concatenate strings
        String strBalance = "Balance: Php " + Double.toString(getCurrentBalance());
        String strAccountNo = "Account No: " + sessionManager.getPreferences(context, "UserAccountNo");

        // set values of textViews
        textViewBalance.setText(strBalance);
        textViewAccountNo.setText(strAccountNo);

        // return inflated layout for this fragment
        return view;

    }// end onCreateView

    @Override
    public void onResume() {

        super.onResume();

        // Set title
        ((UserNavigation) getActivity()).setActionBarTitle(getString(R.string.checkBalance));

    }// end method onResume

    public Double getCurrentBalance() {

        // declaration
        Double dblCurrentBalance = null;

        // fetch balance from user account with matching pin
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT b.dblBankAccountBalance \n" +
                "FROM BankAccount b\n" +
                "WHERE b.intBankAccountID = '" + sessionManager.getPreferences(context, "UserID") + "'" , null);

        if (c.moveToFirst()) {

            do {

                // set bankAccountBalance to dblCurrentBalance
                dblCurrentBalance = c.getDouble(0);

            } while (c.moveToNext());

        }// end if

        c.close();
        db.close();

        // return dblCurrentBalance
        return dblCurrentBalance;

    }// end method getCurrentBalance

}// end class UserBalance