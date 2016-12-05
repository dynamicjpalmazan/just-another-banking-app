package com.example.daniel.bankingapp.Administrator.Maintenance;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.daniel.bankingapp.Database.DatabaseHelper;
import com.example.daniel.bankingapp.Database.Tables.ActivityLog;
import com.example.daniel.bankingapp.Database.Tables.BankAccount;
import com.example.daniel.bankingapp.Database.Tables.Person;
import com.example.daniel.bankingapp.Database.Tables.PersonBankAccount;
import com.example.daniel.bankingapp.Main.MainActivity;
import com.example.daniel.bankingapp.R;
import com.example.daniel.bankingapp.Utility.*;
import com.example.daniel.bankingapp.Utility.SecurityManager;

import java.util.ArrayList;

/**
 * Created by Daniel on 11/29/2016.
 */
public class AdminMaintenanceAddUser extends Fragment {

    // declarations
    Context context;

    // editTexts -> new record form
    EditText editTextNewRecordFName;
    EditText editTextNewRecordLName;
    EditText editTextNewRecordAddress;
    EditText editTextNewRecordInitialDeposit;
    EditText editTextNewRecordPinCode;

    // buttons -> new record form
    Button btnNewRecordProceed;
    Button btnNewRecordCancel;

    // arrayList -> arrayList of EditTexts
    ArrayList<EditText> editTextList = new ArrayList<>();

    // person_bank_account object
    PersonBankAccount objPersonBankAccount = new PersonBankAccount();

    public AdminMaintenanceAddUser(Context context) {

        this.context = context;

    }// end constructor

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_admin_frag_maintenance_add, container, false);

        // assign editTexts to variables
        editTextNewRecordFName = (EditText) view.findViewById(R.id.textNewRecordFName);
        editTextNewRecordLName = (EditText) view.findViewById(R.id.textNewRecordLName);
        editTextNewRecordAddress = (EditText) view.findViewById(R.id.textNewRecordAddress);
        editTextNewRecordInitialDeposit = (EditText) view.findViewById(R.id.textNewRecordInitialDeposit);
        editTextNewRecordPinCode = (EditText) view.findViewById(R.id.textNewRecordPassword);

        // assign buttons to variables
        btnNewRecordProceed = (Button) view.findViewById(R.id.btnNewRecordProceed);
        btnNewRecordCancel = (Button) view.findViewById(R.id.btnNewRecordCancel);

        // btnNewRecordProceed -> onClickListener
        btnNewRecordProceed.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // method call -> onClickProceedNewRecord
                onClickProceedNewRecord();

            }// end onClick

        });// end Listener

        // btnNewRecordCancel -> onClickListener
        btnNewRecordCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // method call -> onClickCancelNewRecord
                onClickCancelNewRecord();

            }// end onClick

        });// end Listener

        // linearLayout -> withdraw layout
        LinearLayout linearRoot = (LinearLayout) view.findViewById(R.id.linearNewRecord);

        // for loop -> check for instances of editTexts
        for(int i = 0; i < linearRoot.getChildCount(); i++) {

            if(linearRoot.getChildAt(i) instanceof EditText) {

                editTextList.add((EditText) linearRoot.getChildAt(i));

            }// add instances of editTexts to arraylist

        }// end for loop -> check for instances of editTexts

        // return inflated layout for this fragment
        return view;

    }// end onCreateView

    public void onClickProceedNewRecord() {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {

                    case DialogInterface.BUTTON_POSITIVE:

                        if (isEmpty()) {

                            // toast -> empty fields
                            Toast.makeText(context, "Empty fields detected!", Toast.LENGTH_SHORT).show();

                        } else {

                            // method call -> submitSignUpValues
                            submitSignUpValues();

                            // method call -> showCredentials
                            showCredentials();

                        }// end empty field validation

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;

                }// end switch -> which

            }// end DialogInterface -> SignUp -> Proceed

        };// end DialogInterface

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // set alert dialog's content
        builder.setMessage("Submit account information?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }// end method onClickProceedNewRecord

    public void onClickCancelNewRecord() {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {

                    case DialogInterface.BUTTON_POSITIVE:

                        // start activity -> AdministratorNavigation
                        Intent intentMain = new Intent(context, MainActivity.class);
                        startActivity(intentMain);

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;

                }// end switch -> which

            }// end DialogInterface -> SignUp -> Cancel

        };// end DialogInterface

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // set alert dialog's content
        builder.setMessage("Are you sure?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }// end method onClickCancelNewRecord

    public void submitSignUpValues() {

        // declarations
        int intPersonID = 0;
        int intBankAccountID = 0;
        long longBaseCardNo = 201600000L;

        // content values -> person
        ContentValues cvPerson = new ContentValues();
        cvPerson.put(Person.KEY_PERSON_FNAME, editTextNewRecordFName.getText().toString().trim());
        cvPerson.put(Person.KEY_PERSON_LNAME, editTextNewRecordLName.getText().toString().trim());
        cvPerson.put(Person.KEY_PERSON_ADDRESS, editTextNewRecordAddress.getText().toString().trim());

        // content values -> bank account
        ContentValues cvBankAccount = new ContentValues();
        cvBankAccount.put(BankAccount.KEY_BANKACCOUNT_BAL, Double.parseDouble(editTextNewRecordInitialDeposit.getText().toString().trim()));
        cvBankAccount.put(BankAccount.KEY_BANKACCOUNT_PIN, SecurityManager.encryptIt(editTextNewRecordPinCode.getText().toString().trim()));

        // DatabaseHelper -> insert
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        dbHelper.insert(Person.TABLE, cvPerson);
        dbHelper.insert(BankAccount.TABLE, cvBankAccount);

        // get max keys from last record inserted
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT MAX(p." + Person.KEY_PERSON_ID + ")," +
                "MAX(b." + BankAccount.KEY_BANKACCOUNT_ID + ") " +
                "FROM " + Person.TABLE + " p, " + BankAccount.TABLE + " b ", null);

        if (c.moveToFirst()) {

            do {

                intPersonID  = c.getInt(0);
                intBankAccountID = c.getInt(1);

            } while (c.moveToNext());

        }// end if

        c.close();
        db.close();

        // content values -> person_bank_account
        ContentValues cvPBA = new ContentValues();
        cvPBA.put(PersonBankAccount.KEY_PBA_ID, (longBaseCardNo + intPersonID));
        cvPBA.put(PersonBankAccount.KEY_PBA_PERSON_ID, intPersonID);
        cvPBA.put(PersonBankAccount.KEY_PBA_BANKACCOUNT_ID, intBankAccountID);

        // DatabaseHelper -> insert
        dbHelper.insert(PersonBankAccount.TABLE, cvPBA);

        // content values -> activity log
        ContentValues cvActivityLog = new ContentValues();
        cvActivityLog.put(ActivityLog.KEY_LOG_PERSON_ID, intPersonID);
        cvActivityLog.put(ActivityLog.KEY_LOG_PROCEDURE, ActivityLog.PROCEDRE_SIGNUP);

        // DatabaseHelper -> insert
        dbHelper.insert(ActivityLog.TABLE, cvActivityLog);

        Toast.makeText(context, "Successfully created a new record!", Toast.LENGTH_SHORT).show();

        // store credentials in an object
        objPersonBankAccount.setBankAccountPin(SecurityManager.encryptIt(editTextNewRecordPinCode.getText().toString().trim()));
        objPersonBankAccount.setPersonBankAccountID(Long.toString(longBaseCardNo + intPersonID));

    }// end method submitSignUpValues

    public void showCredentials() {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {

                    case DialogInterface.BUTTON_NEUTRAL:

                        // start activity -> AdministratorNavigation
                        Intent intentMain = new Intent(context, MainActivity.class);
                        startActivity(intentMain);

                        break;

                }// end switch -> which

            }// end DialogInterface -> SignUp -> Proceed

        };// end DialogInterface

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // set alert dialog's content
        builder.setMessage("Here are your account credentials:" + "\n\n" +
                "Account Number: " + objPersonBankAccount.getPersonBankAccountID() + "\n\n" +
                "Pin Code: " + SecurityManager.decryptIt(objPersonBankAccount.getBankAccountPin()) + "\n")
                .setNeutralButton("Okay", dialogClickListener)
                .show();

    }// end method showCredentials

    public boolean isEmpty() {

        boolean boolIsEmpty = true;

        // for loop -> iterate through editTexts
        for (int intCounter = 0; intCounter < (editTextList.size()); intCounter++) {

            // validate current editText if empty
            boolIsEmpty = Validation.validateEmpty(editTextList.get(intCounter));

        }// end for loop -> iterate through editTexts

        return boolIsEmpty;

    }// end method isEmpty

}// end class AdminMaintenanceAddUser
