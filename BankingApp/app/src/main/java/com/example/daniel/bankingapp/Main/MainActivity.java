package com.example.daniel.bankingapp.Main;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.daniel.bankingapp.Database.DatabaseHelper;
import com.example.daniel.bankingapp.Database.Tables.BankAccount;
import com.example.daniel.bankingapp.Database.Tables.Person;
import com.example.daniel.bankingapp.Database.Tables.PersonBankAccount;
import com.example.daniel.bankingapp.Navigation.AdministratorNavigation;
import com.example.daniel.bankingapp.Navigation.UserNavigation;
import com.example.daniel.bankingapp.R;
import com.example.daniel.bankingapp.Utility.SessionManager;
import com.example.daniel.bankingapp.Utility.Validation;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // global declarations
    // editTexts -> login form
    EditText editTextPinCode;
    EditText editTextAccountNo;

    // arrayList -> arrayList of EditTexts
    ArrayList<EditText> editTextList = new ArrayList<EditText>();

    // session sessionManager
    SessionManager sessionManager;

    // person personObject
    Person person;

    // person_bank_account personBankAccount
    PersonBankAccount personBankAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);

        // create an instance of person & bank account for setting values
        person = new Person();
        personBankAccount = new PersonBankAccount();

        // create an instance of session sessionManager to check if there is an active session
        sessionManager = new SessionManager();
        String strSPStatus = sessionManager.getPreferences(MainActivity.this, "SessionStatus");
        String strSPId = sessionManager.getPreferences(MainActivity.this, "UserID");

        if (strSPStatus.equals("1") && strSPId.equals("0")){

            // start activity -> AdministratorNavigation
            Intent intentAdmin = new Intent(MainActivity.this, AdministratorNavigation.class);
            startActivity(intentAdmin);

        } else if (strSPStatus.equals("1")) {

            // start activity -> UserNavigation
            Intent intentUser = new Intent(MainActivity.this, UserNavigation.class);
            startActivity(intentUser);

        } else {

            // editTexts -> login form
            editTextAccountNo = (EditText) findViewById(R.id.textLoginAccountNo);
            editTextPinCode = (EditText) findViewById(R.id.textLoginPinCode);

            // linearLayout -> sign-up layout
            LinearLayout linearRoot = (LinearLayout) findViewById(R.id.linearLogin);

            // for loop -> check for instances of editTexts
            for(int i = 0; i < linearRoot.getChildCount(); i++) {

                if(linearRoot.getChildAt(i) instanceof EditText) {

                    editTextList.add((EditText) linearRoot.getChildAt(i));

                }// add instances of editTexts to arraylist

            }// end for loop -> check for instances of editTexts

        }// end check for active session

    }// end method onCreate

    public void onClickLogin(View a) {

        // validate input
        if (isEmpty()) {

            // toast -> empty fields
            Toast.makeText(MainActivity.this, "Empty fields detected!", Toast.LENGTH_SHORT).show();

        } else {

            // method call -> validateCredentials
            int intReturnedUserID = validateCredentials();

            switch (intReturnedUserID) {

                case -1:

                    //toast -> invalid user credentials
                    Toast.makeText(MainActivity.this, "Invalid credentials!", Toast.LENGTH_SHORT).show();

                    break;

                case 0:

                    // method call -> setUserPreferences
                    setUserPreferences(intReturnedUserID);

                    // start activity -> AdministratorNavigation
                    Intent intentAdmin = new Intent(MainActivity.this, AdministratorNavigation.class);
                    startActivity(intentAdmin);

                    break;

                default:

                    // method call -> setUserPreferences
                    setUserPreferences(intReturnedUserID);

                    // start activity -> UserNavigation
                    Intent intentUser = new Intent(MainActivity.this, UserNavigation.class);
                    startActivity(intentUser);

                    break;

            }// end switch -> intReturnedUserID

        }// end validate input

    }// end method onClickLogin

    public void onClickSignUp(View a) {

        // start activity -> MainSignUp
        Intent intentUser = new Intent(MainActivity.this, MainSignUp.class);
        startActivity(intentUser);

    }// end method onClickSignUp

    public boolean isEmpty() {

        boolean boolIsEmpty = true;

        // for loop -> iterate through editTexts
        for (int intCounter = 0; intCounter < (editTextList.size()); intCounter++) {

            // validate current editText if empty
            boolIsEmpty = Validation.validateEmpty(editTextList.get(intCounter));

        }// end for loop -> iterate through editTexts

        return boolIsEmpty;

    }// end method isEmpty

    public int validateCredentials() {

        // set default value of personID
        person.setPersonID(-1);

        // assign input values to variables
        String strAccountNo = editTextAccountNo.getText().toString().trim();
        String strPinCode = editTextPinCode.getText().toString().trim();

        // validate credentials
        if (strAccountNo.equals("201600000") && strPinCode.equals("0000")) {

            // if credentials belong to admin, set value then return -> admin
            person.setPersonID(0);
            person.setPersonFName("Administrator");
            person.setPersonLName("Hello");
            person.setPersonAddress("Manila, Philippines");
            personBankAccount.setBankAccountPin(strPinCode);
            personBankAccount.setPersonBankAccountID(strAccountNo);

            // return -> intPersonID
            return person.getPersonID();

        } else {

            // else, validate if credentials belong to user
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cUser = db.rawQuery("SELECT p.intPersonID, p.strPersonFName, p.strPersonLName,\n" +
                    "\t   p.strPersonAddress\n" +
                    "FROM   Person p \n" +
                    "JOIN   PersonBankAccount pb\n" +
                    "\tON p.intPersonID = pb.intPBAPersonID\n" +
                    "JOIN   BankAccount b\n" +
                    "\tON pb.intPBABankAccountID = b.intBankAccountID\n" +
                    "WHERE  pb.strPBAID = '" + strAccountNo + "' \n" +
                    "\tAND b.strBankAccountPassword = '" + strPinCode + "'", null);

            if (cUser.moveToFirst()) {

                do {

                    // set values from cUser cursor to person & personBankAccountObjects
                    person.setPersonID(cUser.getInt(0));
                    person.setPersonFName(cUser.getString(1));
                    person.setPersonLName(cUser.getString(2));
                    person.setPersonAddress(cUser.getString(3));
                    personBankAccount.setBankAccountPin(strPinCode);
                    personBankAccount.setPersonBankAccountID(strAccountNo);

                } while (cUser.moveToNext());

            }// end if

            cUser.close();
            db.close();

        }// end validate credentials

        // return -> intPersonID
        return person.getPersonID();

    }// end method validateCredentials

    public void setUserPreferences(int intReturnedUserID) {

        // set preferences | user ~ administrator
        sessionManager.setPreferences(MainActivity.this, "SessionStatus", "1");
        sessionManager.setPreferences(MainActivity.this, "UserID", Integer.toString(intReturnedUserID));
        sessionManager.setPreferences(MainActivity.this, "UserFName", person.getPersonFName());
        sessionManager.setPreferences(MainActivity.this, "UserLName", person.getPersonLName());
        sessionManager.setPreferences(MainActivity.this, "UserAddress", person.getPersonAddress());
        sessionManager.setPreferences(MainActivity.this, "UserPinCode", personBankAccount.getBankAccountPin());
        sessionManager.setPreferences(MainActivity.this, "UserAccountNo",personBankAccount.getPersonBankAccountID());

    }// end method setUserPreferences

}// end class MainActivity
