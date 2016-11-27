package com.example.daniel.bankingapp.Administrator;

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
import com.example.daniel.bankingapp.R;
import com.example.daniel.bankingapp.Utility.SessionManager;
import com.example.daniel.bankingapp.Utility.Validation;

/**
 * Created by Daniel on 11/25/2016.
 */
public class AdminDeposit extends Fragment {

    // declarations
    Context context;
    SessionManager sessionManager;

    // textViews -> deposit form
    TextView textViewAccountNo;

    // editTexts -> deposit form
    EditText editTextDepositAmount;

    // buttons -> deposit form
    Button btnProceedDeposit;
    Button btnCancelDeposit;

    public AdminDeposit(Context context) {

        this.context = context;

    }// end constructor

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_admin_frag_deposit, container, false);

        // new instances of sessionManager
        sessionManager = new SessionManager();

        // retrieve and set account no. to corresponding textView
        // assign textViews to variables
        textViewAccountNo = (TextView) view.findViewById(R.id.textDepositAccountNo);

        // declare onClick listeners for buttons
        // assign buttons to variables
        btnProceedDeposit = (Button) view.findViewById(R.id.btnDepositProceed);
        btnCancelDeposit = (Button) view.findViewById(R.id.btnDepositCancel);

        // btnProceedDeposit -> onClickListener
        btnProceedDeposit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // method call -> onClickProceedDeposit
                onClickProceedDeposit();

            }// end onClick

        });// end Listener

        // btnCancelDeposit -> onClickListener
        btnCancelDeposit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // method call -> onClickCancelDeposit
                onClickCancelDeposit();

            }// end onClick

        });// end Listener
        // end buttons

        // assign editText to variable
        editTextDepositAmount = (EditText) view.findViewById(R.id.textDepositAmount);

        // return inflated layout for this fragment
        return view;

    }// end onCreateView

    public void onClickProceedDeposit() {

        // new linearLayout -> vertical
        LinearLayout linearLayoutProceedDepositDialog = new LinearLayout(context);
        linearLayoutProceedDepositDialog.setOrientation(LinearLayout.VERTICAL);

        // new layoutParams
        LinearLayout.LayoutParams layoutParamsProceedDepositDialog = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        // layoutParams -> setMargins
        layoutParamsProceedDepositDialog.setMargins(100, 0, 100, 0);

        // new final editTextPinCode
        final EditText editTextPinCode = new EditText(context);

        // allow only numbers | password
        editTextPinCode.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        // set maximum length
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(4);
        editTextPinCode.setFilters(FilterArray);

        // set contents of linearLayout
        linearLayoutProceedDepositDialog
                .addView(editTextPinCode, layoutParamsProceedDepositDialog);

        new AlertDialog.Builder(context)
                .setTitle("Verify identity")
                .setMessage("Please enter your pin code below:")
                .setView(linearLayoutProceedDepositDialog)
                .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        String strPinCode = editTextPinCode.getText().toString().trim();

                        if (strPinCode.equals(sessionManager.getPreferences(context, "UserPinCode"))) {

                            Double dblDepositAmount = Double.parseDouble(editTextDepositAmount.getText().toString());

                            if (isNegative()) {

                                // toast -> negative input
                                Toast.makeText(context, "Enter a positive integer!", Toast.LENGTH_SHORT).show();

                            } else if (dblDepositAmount > getCurrentBalance()) {

                                // toast -> amount too large
                                Toast.makeText(context, "Invalid amount!", Toast.LENGTH_SHORT).show();

                            } else {

                                // proceed with deposit
                                depositAmount();

                                // toast -> successfully deposited
                                Toast.makeText(context, "Successfully deposited from your account!", Toast.LENGTH_SHORT).show();

                                // start activity -> UserNavigation
                                Intent intentMain = new Intent(context, MainActivity.class);
                                startActivity(intentMain);

                            }// end amount validation

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

    }// end method onClickProceedDeposit

    public void onClickCancelDeposit() {

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

    }// end method onClickCancelDeposit

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

    public void depositAmount() {

        Double dblDepositAmount = Double.parseDouble(editTextDepositAmount.getText().toString());

        // content values -> bank account
        ContentValues cvDeposit = new ContentValues();
        cvDeposit.put(BankAccount.KEY_BANKACCOUNT_BAL, getCurrentBalance() + dblDepositAmount);

        // DatabaseHelper -> update
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        dbHelper.update(BankAccount.TABLE,
                cvDeposit,
                BankAccount.KEY_BANKACCOUNT_ID + "= ?",
                new String[]{String.valueOf(sessionManager.getPreferences(context, "UserID"))});

    }// end method depositAmount

    public boolean isNegative() {

        // validate if editText content is negative
        boolean boolIsNegative = Validation.validateNegative(editTextDepositAmount);

        return boolIsNegative;

    }// end method isNegative

}// end class AdminDeposit