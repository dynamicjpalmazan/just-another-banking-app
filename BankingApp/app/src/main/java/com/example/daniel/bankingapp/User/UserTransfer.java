package com.example.daniel.bankingapp.User;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.daniel.bankingapp.Database.DatabaseHelper;
import com.example.daniel.bankingapp.Database.Tables.BankAccount;
import com.example.daniel.bankingapp.Main.MainActivity;
import com.example.daniel.bankingapp.Navigation.UserNavigation;
import com.example.daniel.bankingapp.R;
import com.example.daniel.bankingapp.Utility.SessionManager;
import com.example.daniel.bankingapp.Utility.Validation;

/**
 * Created by Daniel on 11/20/2016.
 */
public class UserTransfer extends Fragment {

    // declarations
    Context context;
    SessionManager sessionManager;

    // textViews -> transfer form
    TextView textViewAccountNo;
    TextView textViewBalance;

    // editTexts -> transfer form
    EditText editTextTransferAmount;
    EditText editTextTransferAccount;

    // buttons -> transfer form
    Button btnProceedTransfer;
    Button btnCancelTransfer;

    public UserTransfer(Context context) {

        this.context = context;

    }// end constructor

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_user_frag_transfer, container, false);

        // new instances of sessionManager
        sessionManager = new SessionManager();

        // retrieve and set account no. to corresponding textView
            // assign textViews to variables
            textViewAccountNo = (TextView) view.findViewById(R.id.textTransferAccountNo);
            textViewBalance = (TextView) view.findViewById(R.id.textTransferBalance);
            // concatenate strings
            String strAccountNo = "Account No: " + sessionManager.getPreferences(context, "UserAccountNo");
            // set values of textViews
            textViewAccountNo.setText(strAccountNo);
            textViewBalance.setText("Balance: " + Double.toString(getCurrentBalanceFrom()));
        // end

        // declare onClick listeners for buttons
            // assign buttons to variables
            btnProceedTransfer = (Button) view.findViewById(R.id.btnTransferProceed);
            btnCancelTransfer = (Button) view.findViewById(R.id.btnTransferCancel);

            // btnProceedTransfer -> onClickListener
            btnProceedTransfer.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    // method call -> onClickProceedTransfer
                    onClickProceedTransfer();

                }// end onClick

            });// end Listener

            // btnCancelTransfer -> onClickListener
            btnCancelTransfer.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    // method call -> onClickProceedTransfer
                    onClickCancelTransfer();

                }// end onClick

            });// end Listener
        // end buttons

        // assign editText to variable
        editTextTransferAmount = (EditText) view.findViewById(R.id.textTransferAmount);
        editTextTransferAccount = (EditText) view.findViewById(R.id.textTransferReceiverAccount);

        // return inflated layout for this fragment
        return view;

    }// end onCreateView

    @Override
    public void onResume() {

        super.onResume();

        // Set title
        ((UserNavigation) getActivity()).setActionBarTitle(getString(R.string.moneyTransfer));

    }// end method onResume

    public void onClickProceedTransfer() {

        // new linearLayout -> vertical
        LinearLayout linearLayoutProceedTransferDialog = new LinearLayout(context);
        linearLayoutProceedTransferDialog.setOrientation(LinearLayout.VERTICAL);

        // new layoutParams
        LinearLayout.LayoutParams layoutParamsProceedTransferDialog = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        // layoutParams -> setMargins
        layoutParamsProceedTransferDialog.setMargins(100, 0, 100, 0);

        // new final editTextPinCode
        final EditText editTextPinCode = new EditText(context);

        // allow only numbers | password
        editTextPinCode.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        // set maximum length
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(4);
        editTextPinCode.setFilters(FilterArray);

        // set contents of linearLayout
        linearLayoutProceedTransferDialog
                .addView(editTextPinCode, layoutParamsProceedTransferDialog);

        new AlertDialog.Builder(context)
                .setTitle("Verify identity")
                .setMessage("Please enter your pin code below:")
                .setView(linearLayoutProceedTransferDialog)
                .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        String strPinCode = editTextPinCode.getText().toString().trim();

                        if (strPinCode.equals(sessionManager.getPreferences(context, "UserPinCode"))) {

                            if (isExisting() == null) {

                                // toast -> non-existent account no
                                Toast.makeText(context, "Incorrect account number of receiver!", Toast.LENGTH_SHORT).show();

                            } else {

                                Double dblTransferAmount = Double.parseDouble(editTextTransferAmount.getText().toString());

                                if (isNegative()) {

                                    // toast -> negative input
                                    Toast.makeText(context, "Enter a positive integer!", Toast.LENGTH_SHORT).show();

                                } else if (dblTransferAmount > getCurrentBalanceFrom()) {

                                    // toast -> amount too large
                                    Toast.makeText(context, "Invalid amount!", Toast.LENGTH_SHORT).show();

                                } else {

                                    // proceed with transfer
                                    transferAmount();

                                    // toast -> successfully transferred
                                    Toast.makeText(context, "Successfully transferred money from your account!", Toast.LENGTH_SHORT).show();

                                    // start activity -> UserNavigation
                                    Intent intentMain = new Intent(context, MainActivity.class);
                                    startActivity(intentMain);

                                }// end amount validation

                            }// end receiver account no. validation

                        } else {

                            // toast -> empty fields
                            Toast.makeText(context, "Invalid pin code!", Toast.LENGTH_SHORT).show();

                        }// end pin validation

                    }// end positive onClick

                })// end set positive button

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        // do something

                    }// end negative onClick

                })// end negative button

                .show();

    }// end method onClickProceedTransfer

    public void onClickCancelTransfer() {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {

                    case DialogInterface.BUTTON_POSITIVE:

                        // start activity -> UserNavigation
                        Intent intentMain = new Intent(context, MainActivity.class);
                        startActivity(intentMain);

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;

                }// end switch -> which

            }// end DialogInterface -> Logout -> onClickLogin

        };// end DialogInterface

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // set alert dialog's content
        builder.setMessage("Are you sure?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }// end method onClickCancelTransfer

    public String isExisting() {

        String strAccountID = null;
        String strReceiverAccountNo = editTextTransferAccount.getText().toString().trim();

        // fetch record from user account with matching pin as entered
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT pb.strPBAID, b.dblBankAccountBalance\n" +
                "FROM PersonBankAccount pb\n" +
                "JOIN BankAccount b\n" +
                "ON pb.intPBABankAccountID = \n" +
                "b.intBankAccountID\n" +
                "WHERE pb.strPBAID = '" + strReceiverAccountNo + "'" , null);

        if (c.moveToFirst()) {

            do {

                // set personBankAccountID to strAccountID
                strAccountID = c.getString(0);

            } while (c.moveToNext());

        }// end if

        c.close();
        db.close();

        // return strAccountID
        return strAccountID;

    }// end method isExisting

    public int getBankAccountID() {

        // declarations
        int intBankAccountID = 0;
        String strReceiverAccountNo = editTextTransferAccount.getText().toString().trim();

        // fetch record from user account with matching pin as entered
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT b.intBankAccountID " +
                "FROM BankAccount b\n" +
                "JOIN PersonBankAccount pb\n" +
                "ON b.intBankAccountID = \n" +
                "pb.intPBABankAccountID\n" +
                "WHERE pb.strPBAID = '" + strReceiverAccountNo + "'" , null);

        if (c.moveToFirst()) {

            do {

                // set personBankAccountID to strAccountID
                intBankAccountID = c.getInt(0);

            } while (c.moveToNext());

        }// end if

        c.close();
        db.close();

        // return strAccountID
        return intBankAccountID;

    }// end method getBankAccountID

    public Double getCurrentBalanceFrom() {

        // declaration
        Double dblCurrentBalanceFrom = null;

        // fetch balance from user account with matching pin
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT b.dblBankAccountBalance \n" +
                "FROM BankAccount b\n" +
                "WHERE b.intBankAccountID = '" + sessionManager.getPreferences(context, "UserID") + "'" , null);

        if (c.moveToFirst()) {

            do {

                // set bankAccountBalance to dblCurrentBalanceFrom
                dblCurrentBalanceFrom = c.getDouble(0);

            } while (c.moveToNext());

        }// end if

        c.close();
        db.close();

        // return dblCurrentBalanceFrom
        return dblCurrentBalanceFrom;

    }// end method getCurrentBalanceFrom

    public Double getCurrentBalanceTo() {

        // declaration
        Double dblCurrentBalanceTo = null;
        String strReceiverAccountNo = editTextTransferAccount.getText().toString().trim();

        // fetch balance from user account with matching pin
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT pb.strPBAID, b.dblBankAccountBalance\n" +
                "FROM PersonBankAccount pb\n" +
                "JOIN BankAccount b\n" +
                "ON pb.intPBABankAccountID = \n" +
                "b.intBankAccountID\n" +
                "WHERE pb.strPBAID = '" + strReceiverAccountNo + "'" , null);

        if (c.moveToFirst()) {

            do {

                // set bankAccountBalance to dblCurrentBalanceTo
                dblCurrentBalanceTo = c.getDouble(1);

            } while (c.moveToNext());

        }// end if

        c.close();
        db.close();

        // return dblCurrentBalanceTo
        return dblCurrentBalanceTo;

    }// end method getCurrentBalanceTo

    public void transferAmount() {

        Double dblTransferAmount = Double.parseDouble(editTextTransferAmount.getText().toString());
        String strReceiverAccountNo = editTextTransferAccount.getText().toString().trim();

        // get id (not acct no.) of receiver

        // start -> subtracting transferred amount from own account

            // content values -> bank account
            ContentValues cvTransferFrom = new ContentValues();
            cvTransferFrom.put(BankAccount.KEY_BANKACCOUNT_BAL, getCurrentBalanceFrom() - dblTransferAmount);

            // DatabaseHelper -> update
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            dbHelper.update(BankAccount.TABLE,
                            cvTransferFrom,
                            BankAccount.KEY_BANKACCOUNT_ID + "= ?",
                            new String[]{String.valueOf(sessionManager.getPreferences(context, "UserID"))});

        // end -> subtract

        // start -> adding transferred amount to selected account

            // content values -> bank account
            ContentValues cvTransferTo = new ContentValues();
            cvTransferTo.put(BankAccount.KEY_BANKACCOUNT_BAL, getCurrentBalanceTo() + dblTransferAmount);

            // DatabaseHelper -> update
            DatabaseHelper dbHelperTo = new DatabaseHelper(context);
            dbHelperTo.update(BankAccount.TABLE,
                            cvTransferTo,
                            BankAccount.KEY_BANKACCOUNT_ID + "= ?",
                            new String[]{String.valueOf(getBankAccountID())});

        // start -> add transferred amount to account

        // end -> add

    }// end method transferAmount

    public boolean isNegative() {

        // validate if editText content is negative
        boolean boolIsNegative = Validation.validateNegative(editTextTransferAmount);

        return boolIsNegative;

    }// end method isNegative

}// end class UserTransfer