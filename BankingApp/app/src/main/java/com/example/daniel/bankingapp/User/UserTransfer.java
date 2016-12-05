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
import com.example.daniel.bankingapp.Database.Tables.ActivityLog;
import com.example.daniel.bankingapp.Database.Tables.BankAccount;
import com.example.daniel.bankingapp.Main.MainActivity;
import com.example.daniel.bankingapp.Navigation.UserNavigation;
import com.example.daniel.bankingapp.R;
import com.example.daniel.bankingapp.Utility.*;
import com.example.daniel.bankingapp.Utility.SecurityManager;

import java.text.DecimalFormat;
import java.util.ArrayList;

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

    // arrayList -> arrayList of EditTexts
    ArrayList<EditText> editTextList = new ArrayList<EditText>();

    DecimalFormat decFormat = new DecimalFormat("0.00");

    public UserTransfer(Context context) {

        this.context = context;

    }// end constructor

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_user_frag_transfer, container, false);

        // new instances of sessionManager
        sessionManager = new SessionManager();

        // assign editTexts to variables
        editTextTransferAmount = (EditText) view.findViewById(R.id.textTransferAmount);
        editTextTransferAccount = (EditText) view.findViewById(R.id.textTransferReceiverAccount);

        // assign textViews to variables
        textViewAccountNo = (TextView) view.findViewById(R.id.textTransferAccountNo);
        textViewBalance = (TextView) view.findViewById(R.id.textTransferBalance);

        // assign buttons to variables
        btnProceedTransfer = (Button) view.findViewById(R.id.btnTransferProceed);
        btnCancelTransfer = (Button) view.findViewById(R.id.btnTransferCancel);

        // concatenate strings
        String strAccountNo = "Account No: " + sessionManager.getPreferences(context, "UserAccountNo");
        String strBalance = "Balance: Php " + decFormat.format(getCurrentBalance(Integer.parseInt(sessionManager.getPreferences(context, "UserID"))));

        // set values of textViews
        textViewAccountNo.setText(strAccountNo);
        textViewBalance.setText(strBalance);

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

        // linearLayout -> withdraw layout
        LinearLayout linearRoot = (LinearLayout) view.findViewById(R.id.linearUserTransfer);

        // for loop -> check for instances of editTexts
        for(int i = 0; i < linearRoot.getChildCount(); i++) {

            if(linearRoot.getChildAt(i) instanceof EditText) {

                editTextList.add((EditText) linearRoot.getChildAt(i));

            }// add instances of editTexts to arraylist

        }// end for loop -> check for instances of editTexts

        // return inflated layout for this fragment
        return view;

    }// end onCreateView

    public void onClickProceedTransfer() {

        if (isEmpty()) {

            // toast -> empty fields
            Toast.makeText(context, "Empty fields detected!", Toast.LENGTH_SHORT).show();

        } else {

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

                            // initialize necessary variables
                            String strPinCode = editTextPinCode.getText().toString().trim();
                            int intSenderBankAccountID = Integer.parseInt(sessionManager.getPreferences(context, "UserID"));
                            int intReceiverBankAccountID = getBankAccountID(editTextTransferAccount.getText().toString().trim());
                            Double dblTransferAmount = Double.parseDouble(editTextTransferAmount.getText().toString());

                            if (strPinCode.equals(SecurityManager.decryptIt(sessionManager.getPreferences(context, "UserPinCode")))) {

                                if (intReceiverBankAccountID == 0) {

                                    // toast -> non-existent account no
                                    Toast.makeText(context, "Account no. does not exist!", Toast.LENGTH_SHORT).show();

                                } else if (isNegative()) {

                                    // toast -> negative input
                                    Toast.makeText(context, "Enter a positive integer!", Toast.LENGTH_SHORT).show();

                                } else if (intSenderBankAccountID == intReceiverBankAccountID) {

                                    // toast -> same account id
                                    Toast.makeText(context, "Invalid account number!", Toast.LENGTH_SHORT).show();

                                } else if (dblTransferAmount > getCurrentBalance(intSenderBankAccountID)) {

                                    // toast -> amount too large
                                    Toast.makeText(context, "Invalid amount!", Toast.LENGTH_SHORT).show();

                                } else {

                                    // proceed with transfer
                                    transferAmount(intSenderBankAccountID, intReceiverBankAccountID);

                                    // toast -> successfully transferred
                                    Toast.makeText(context, "Successfully transferred money from your account!", Toast.LENGTH_SHORT).show();

                                    // start activity -> AdminNavigation
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

                // set bankAccountBalance to dblCurrentBalanceFrom
                dblCurrentBalance = c.getDouble(0);

            } while (c.moveToNext());

        }// end if

        c.close();
        db.close();

        // return dblCurrentBalance
        return dblCurrentBalance;

    }// end method getCurrentBalance

    public void transferAmount(int intSenderBankAccountID, int intReceiverBankAccountID) {

        Double dblTransferAmount = Double.parseDouble(editTextTransferAmount.getText().toString());
        String strReceiverAccountNo = editTextTransferAccount.getText().toString().trim();

        // content values -> bank account -> sender
        ContentValues cvTransferFrom = new ContentValues();
        cvTransferFrom.put(BankAccount.KEY_BANKACCOUNT_BAL, getCurrentBalance(intSenderBankAccountID) - dblTransferAmount);

        // DatabaseHelper -> update
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        dbHelper.update(BankAccount.TABLE,
                        cvTransferFrom,
                        BankAccount.KEY_BANKACCOUNT_ID + "= ?",
                        new String[]{String.valueOf(sessionManager.getPreferences(context, "UserID"))});

        // content values -> bank account -> receiver
        ContentValues cvTransferTo = new ContentValues();
        cvTransferTo.put(BankAccount.KEY_BANKACCOUNT_BAL, getCurrentBalance(intReceiverBankAccountID) + dblTransferAmount);

        // DatabaseHelper -> update
        DatabaseHelper dbHelperTo = new DatabaseHelper(context);
        dbHelperTo.update(BankAccount.TABLE,
                          cvTransferTo,
                          BankAccount.KEY_BANKACCOUNT_ID + "= ?",
                          new String[]{String.valueOf(intReceiverBankAccountID)});

        // content values -> activity log
        ContentValues cvActivityLog = new ContentValues();
        cvActivityLog.put(ActivityLog.KEY_LOG_PERSON_ID, sessionManager.getPreferences(context, "UserID"));
        cvActivityLog.put(ActivityLog.KEY_LOG_PROCEDURE,
                ActivityLog.PROCEDRE_TRANSFER + " Php " + editTextTransferAmount.getText().toString().trim() +
                        " to Account No. " + strReceiverAccountNo);

        // DatabaseHelper -> insert
        dbHelper.insert(ActivityLog.TABLE, cvActivityLog);

    }// end method transferAmount

    public boolean isNegative() {

        // validate if editText content is negative
        boolean boolIsNegative = Validation.validateNegative(editTextTransferAmount);

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

}// end class UserTransfer