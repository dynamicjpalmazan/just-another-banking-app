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
import com.example.daniel.bankingapp.Database.Tables.Person;
import com.example.daniel.bankingapp.Main.MainActivity;
import com.example.daniel.bankingapp.Navigation.AdministratorNavigation;
import com.example.daniel.bankingapp.R;
import com.example.daniel.bankingapp.Utility.*;
import com.example.daniel.bankingapp.Utility.SecurityManager;

import java.util.ArrayList;

/**
 * Created by Daniel on 11/27/2016.
 */
public class AdminTransfer extends Fragment{

    // declarations
    Context context;
    SessionManager sessionManager;

    // editTexts -> transfer form
    EditText editTextSenderAccountNo;
    EditText editTextReceiverAccountNo;
    EditText editTextTransferAmount;

    // buttons -> transfer form
    Button btnProceedTransfer;
    Button btnCancelTransfer;

    // arrayList -> arrayList of EditTexts
    ArrayList<EditText> editTextList = new ArrayList<EditText>();

    public AdminTransfer(Context context) {

        this.context = context;

    }// end constructor

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_admin_frag_transfer, container, false);

        // new instances of sessionManager
        sessionManager = new SessionManager();

        // assign editText to variable
        editTextSenderAccountNo = (EditText) view.findViewById(R.id.textTransferSenderAccountNo);
        editTextReceiverAccountNo = (EditText) view.findViewById(R.id.textTransferReceiverAccountNo);
        editTextTransferAmount = (EditText) view.findViewById(R.id.textTransferAmount);

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

        // linearLayout -> withdraw layout
        LinearLayout linearRoot = (LinearLayout) view.findViewById(R.id.linearAdminTransfer);

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
                           int intSenderBankAccountID = getBankAccountID(editTextSenderAccountNo.getText().toString().trim());
                           int intReceiverBankAccountID = getBankAccountID(editTextReceiverAccountNo.getText().toString().trim());
                           Double dblTransferAmount = Double.parseDouble(editTextTransferAmount.getText().toString());

                           if (strPinCode.equals(SecurityManager.decryptIt(sessionManager.getPreferences(context, "UserPinCode")))) {

                               if (intSenderBankAccountID == 0) {

                                   // toast -> non-existent sender account no
                                   Toast.makeText(context, "Account no. of sender doesn't exist!", Toast.LENGTH_SHORT).show();

                               } else if (intReceiverBankAccountID == 0) {

                                   // toast -> non-existent receiver account no
                                   Toast.makeText(context, "Account no. of receiver doesn't exist!", Toast.LENGTH_SHORT).show();

                               } else if (isNegative()) {

                                   // toast -> negative input
                                   Toast.makeText(context, "Enter a positive integer!", Toast.LENGTH_SHORT).show();

                               } else if (intSenderBankAccountID == intReceiverBankAccountID) {

                                   // toast -> same account id
                                   Toast.makeText(context, "You entered identical account no. for the sender and receiver!", Toast.LENGTH_SHORT).show();

                               } else if (dblTransferAmount > getCurrentBalance(intSenderBankAccountID)) {

                                   // toast -> amount too large
                                   Toast.makeText(context, "Invalid amount!", Toast.LENGTH_SHORT).show();

                               } else {

                                   // proceed with transfer
                                   transferAmount(intSenderBankAccountID, intReceiverBankAccountID);

                                   // toast -> successfully transferred
                                   Toast.makeText(context, "Successfully transferred money between accounts!", Toast.LENGTH_SHORT).show();

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

                   })// end set negative button

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

        // initialize necessary variables for transaction
        Double dblTransferAmount = Double.parseDouble(editTextTransferAmount.getText().toString());

        // content values -> bank account -> sender
        ContentValues cvTransferFrom = new ContentValues();
        cvTransferFrom.put(BankAccount.KEY_BANKACCOUNT_BAL,
                       getCurrentBalance(intSenderBankAccountID) - dblTransferAmount);

        // DatabaseHelper -> update
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        dbHelper.update(BankAccount.TABLE,
                cvTransferFrom,
                BankAccount.KEY_BANKACCOUNT_ID + "= ?",
                new String[]{String.valueOf(intSenderBankAccountID)});

        // content values -> bank account -> receiver
        ContentValues cvTransferTo = new ContentValues();
        cvTransferTo.put(BankAccount.KEY_BANKACCOUNT_BAL,
                getCurrentBalance(intReceiverBankAccountID) + dblTransferAmount);

        // DatabaseHelper -> update
        DatabaseHelper dbHelperTo = new DatabaseHelper(context);
        dbHelperTo.update(BankAccount.TABLE,
                cvTransferTo,
                BankAccount.KEY_BANKACCOUNT_ID + "= ?",
                new String[]{String.valueOf(intReceiverBankAccountID)});

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

}// end class AdminTransfer