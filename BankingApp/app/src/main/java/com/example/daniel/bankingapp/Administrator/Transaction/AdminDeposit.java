package com.example.daniel.bankingapp.Administrator.Transaction;

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
import android.widget.Toast;

import com.example.daniel.bankingapp.Database.DatabaseHelper;
import com.example.daniel.bankingapp.Database.Tables.BankAccount;
import com.example.daniel.bankingapp.Main.MainActivity;
import com.example.daniel.bankingapp.R;
import com.example.daniel.bankingapp.Utility.*;
import com.example.daniel.bankingapp.Utility.SecurityManager;

import java.util.ArrayList;

/**
 * Created by Daniel on 11/25/2016.
 */
public class AdminDeposit extends Fragment {

    // declarations
    Context context;
    SessionManager sessionManager;

    // editTexts -> deposit form
    EditText editTextDepositAmount;
    EditText editTextAccountNo;

    // buttons -> deposit form
    Button btnProceedDeposit;
    Button btnCancelDeposit;

    // arrayList -> arrayList of EditTexts
    ArrayList<EditText> editTextList = new ArrayList<EditText>();

    public AdminDeposit(Context context) {

        this.context = context;

    }// end constructor

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_admin_frag_deposit, container, false);

        // new instances of sessionManager
        sessionManager = new SessionManager();

        // assign editTexts to variable
        editTextDepositAmount = (EditText) view.findViewById(R.id.textDepositAmount);
        editTextAccountNo = (EditText) view.findViewById(R.id.textDepositAccountNo);

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

        // linearLayout -> withdraw layout
        LinearLayout linearRoot = (LinearLayout) view.findViewById(R.id.linearAdminDeposit);

        // for loop -> check for instances of editTexts
        for(int i = 0; i < linearRoot.getChildCount(); i++) {

            if(linearRoot.getChildAt(i) instanceof EditText) {

                editTextList.add((EditText) linearRoot.getChildAt(i));

            }// add instances of editTexts to arraylist

        }// end for loop -> check for instances of editTexts

        // return inflated layout for this fragment
        return view;

    }// end onCreateView

    public void onClickProceedDeposit() {

        if (isEmpty()) {

            // toast -> empty fields
            Toast.makeText(context, "Empty fields detected!", Toast.LENGTH_SHORT).show();

        } else if (isInvalidAmount()) {

            // toast -> not minimum deposit
            Toast.makeText(context, "Enter a valid amount!", Toast.LENGTH_SHORT).show();

        } else {

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

                            if (strPinCode.equals(SecurityManager.decryptIt(sessionManager.getPreferences(context, "UserPinCode")))) {

                                // initialize necessary variables for validations and transactions
                                Double dblDepositAmount = Double.parseDouble(editTextDepositAmount.getText().toString());
                                String strAccountNo = editTextAccountNo.getText().toString().trim();
                                int intBankAccountID = getBankAccountID(strAccountNo);

                                if (intBankAccountID == 0) {

                                    // toast -> non-existent account no.
                                    Toast.makeText(context, "Account no. doesn't exist!", Toast.LENGTH_SHORT).show();

                                } else if (isNegative()) {

                                    // toast -> negative input
                                    Toast.makeText(context, "Enter a positive integer!", Toast.LENGTH_SHORT).show();

                                } else {

                                    // proceed with deposit
                                    depositAmount(intBankAccountID);

                                    // toast -> successfully deposited
                                    Toast.makeText(context, "Successfully deposited to the account!", Toast.LENGTH_SHORT).show();

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

        }// end empty field validation


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

    public int getBankAccountID(String strAccountNo) {

        // declarations
        int intBankAccountID = 0;

        // fetch record from user account with matching pin as entered
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT b.intBankAccountID " +
                "FROM BankAccount b\n" +
                "JOIN PersonBankAccount pb\n" +
                "ON b.intBankAccountID = \n" +
                "pb.intPBABankAccountID\n" +
                "WHERE pb.strPBAID = '" + strAccountNo + "'" , null);

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

    public Double getCurrentBalance(int intBankAccountID) {

        // declaration
        Double dblCurrentBalance = null;

        // fetch balance from user account with matching pin
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT b.dblBankAccountBalance \n" +
                "FROM BankAccount b\n" +
                "WHERE b.intBankAccountID = '" + intBankAccountID + "'" , null);

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

    public void depositAmount(int intBankAccountID) {

        Double dblDepositAmount = Double.parseDouble(editTextDepositAmount.getText().toString());

        // content values -> bank account
        ContentValues cvDeposit = new ContentValues();
        cvDeposit.put(BankAccount.KEY_BANKACCOUNT_BAL, getCurrentBalance(intBankAccountID) + dblDepositAmount);

        // DatabaseHelper -> update
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        dbHelper.update(BankAccount.TABLE,
                cvDeposit,
                BankAccount.KEY_BANKACCOUNT_ID + "= ?",
                new String[]{String.valueOf(intBankAccountID)});

    }// end method depositAmount

    public boolean isNegative() {

        // validate if editText content is negative
        boolean boolIsNegative = Validation.validateNegative(editTextDepositAmount);

        return boolIsNegative;

    }// end method isNegative

    public boolean isEmpty() {

        boolean boolIsEmpty = true;

        // for loop -> iterate through editTexts
        for (int intCounter = 0; intCounter < (editTextList.size()); intCounter++) {

            // validate current editText if empty
            boolIsEmpty = Validation.validateEmpty(editTextList.get(intCounter));

        }// end for loop -> iterate through editTexts

        return boolIsEmpty;

    }// end method isEmpty

    public boolean isInvalidAmount() {

        boolean boolIsNotMinimum = true;

        // validate current editText if contains valid deposit amounts
        boolIsNotMinimum = Validation.validateAmount(editTextDepositAmount);

        return boolIsNotMinimum;

    }// end method isInvalidAmount

}// end class AdminDeposit