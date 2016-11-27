package com.example.daniel.bankingapp.Main;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.daniel.bankingapp.Database.DatabaseHelper;
import com.example.daniel.bankingapp.Database.Tables.BankAccount;
import com.example.daniel.bankingapp.Database.Tables.Person;
import com.example.daniel.bankingapp.Database.Tables.PersonBankAccount;
import com.example.daniel.bankingapp.Navigation.UserNavigation;
import com.example.daniel.bankingapp.R;
import com.example.daniel.bankingapp.Utility.Validation;

import java.util.ArrayList;

/**
 * Created by Daniel on 11/22/2016.
 */
public class MainSignUp extends AppCompatActivity {

    // global declarations
    // editTexts -> sign-up form
    EditText editTextFName;
    EditText editTextLName;
    EditText editTextAddress;
    EditText editTextIDeposit;
    EditText editTextPinCode;

    // arrayList -> arrayList of EditTexts
    ArrayList<EditText> editTextList = new ArrayList<EditText>();

    // person_bank_account object
    PersonBankAccount objPersonBankAccount = new PersonBankAccount();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_signup);

        // editTexts -> sign-up form
        editTextFName = (EditText) findViewById(R.id.textSignUpFName);
        editTextLName = (EditText) findViewById(R.id.textSignUpLName);
        editTextAddress = (EditText) findViewById(R.id.textSignUpAddress);
        editTextIDeposit = (EditText) findViewById(R.id.textSignUpInitialDeposit);
        editTextPinCode = (EditText) findViewById(R.id.textSignUpPassword);

        // linearLayout -> sign-up layout
        LinearLayout linearRoot = (LinearLayout) findViewById(R.id.linearSignUp);

        // for loop -> check for instances of editTexts
        for(int i = 0; i < linearRoot.getChildCount(); i++) {

            if(linearRoot.getChildAt(i) instanceof EditText) {

                editTextList.add((EditText) linearRoot.getChildAt(i));

            }// add instances of editTexts to arraylist

        }// end for loop -> check for instances of editTexts

    }// end method onCreate

    public void onClickSubmitSignUp(View a) {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {

                    case DialogInterface.BUTTON_POSITIVE:

                        // method call -> validateSignUp
                        validateSignUp();

                        // method call -> submitSignUpValues
                        submitSignUpValues();

                        // method call -> showCredentials
                        showCredentials();

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;

                }// end switch -> which

            }// end DialogInterface -> SignUp -> Proceed

        };// end DialogInterface

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // set alert dialog's content
        builder.setMessage("Submit account information?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }// end method onClickSubmitSignUp

    public void onClickCancelSignUp(View a) {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {

                    case DialogInterface.BUTTON_POSITIVE:
                        // start activity -> MainSignUp
                        Intent intentCancelSignUp = new Intent(MainSignUp.this, MainActivity.class);
                        startActivity(intentCancelSignUp);


                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;

                }// end switch -> which

            }// end DialogInterface -> SignUp -> Cancel

        };// end DialogInterface

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // set alert dialog's content
        builder.setMessage("Cancel sign-up?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }// end method onClickCancelSignUp

    public void validateSignUp() {

        // for loop -> iterate through editTexts
        for (int intCounter = 0; intCounter < (editTextList.size()); intCounter++) {

            Validation.validateEmpty(editTextList.get(intCounter));

        }// end for loop -> iterate through editTexts

        return;

    }// end method validateSignUp

    public void submitSignUpValues() {

        // declarations
        int intPersonID = 0;
        int intBankAccountID = 0;
        long longBaseCardNo = 201600000L;

        // content values -> person
        ContentValues cvPerson = new ContentValues();
        cvPerson.put(Person.KEY_PERSON_FNAME, editTextFName.getText().toString().trim());
        cvPerson.put(Person.KEY_PERSON_LNAME, editTextLName.getText().toString().trim());
        cvPerson.put(Person.KEY_PERSON_ADDRESS, editTextAddress.getText().toString().trim());

        // content values -> bank account
        ContentValues cvBankAccount = new ContentValues();
        cvBankAccount.put(BankAccount.KEY_BANKACCOUNT_BAL, Double.parseDouble(editTextIDeposit.getText().toString().trim()));
        cvBankAccount.put(BankAccount.KEY_BANKACCOUNT_PIN, editTextPinCode.getText().toString().trim());

        // DatabaseHelper -> insert
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.insert(Person.TABLE, cvPerson);
        dbHelper.insert(BankAccount.TABLE, cvBankAccount);
        //** insertion to PersonBankAccount table is made later

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

        Toast.makeText(MainSignUp.this, "Successfully signep-up!", Toast.LENGTH_SHORT).show();

        // store credentials in an object
        objPersonBankAccount.setBankAccountPin(editTextPinCode.getText().toString().trim());
        objPersonBankAccount.setPersonBankAccountID(Long.toString(longBaseCardNo + intPersonID));

        return;

    }// end method submitSignUpValues

    public void showCredentials() {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {

                    case DialogInterface.BUTTON_NEUTRAL:

                        // start activity -> MainActivity
                        Intent intentUser = new Intent(MainSignUp.this, MainActivity.class);
                        startActivity(intentUser);

                }// end switch -> which

            }// end DialogInterface -> SignUp -> Proceed

        };// end DialogInterface

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // set alert dialog's content
        builder.setMessage("Here are your account credentials:" + "\n\n" +
                           "Account Number: " + objPersonBankAccount.getPersonBankAccountID() + "\n\n" +
                           "Pin Code: " + objPersonBankAccount.getBankAccountPin() + "\n")
                .setNeutralButton("Okay", dialogClickListener)
                .show();

    }// end method showCredentials

}// end class MainSignUp