package com.example.daniel.bankingapp.Administrator.Maintenance;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.example.daniel.bankingapp.R;
import com.example.daniel.bankingapp.Utility.*;
import com.example.daniel.bankingapp.Utility.SecurityManager;

import java.util.ArrayList;

/**
 * Created by Daniel on 11/29/2016.
 */
public class AdminMaintenanceDetail extends Fragment {

    // declarations
    Context context;
    SessionManager sessionManager;

    // editTexts -> maintenance detail form
    EditText editTextMaintenanceFName;
    EditText editTextMaintenanceLName;
    EditText editTextMaintenanceAddress;
    EditText editTextMaintenanceCurrentPinCode;
    EditText editTextMaintenanceNewPinCode;

    // buttons -> maintenance detail form
    Button btnProceedMaintenance;
    Button btnCancelMaintenance;

    // arrayList -> arrayList of EditTexts
    ArrayList<EditText> editTextList = new ArrayList<EditText>();

    public AdminMaintenanceDetail(Context context) {

        this.context = context;

    }// end constructor

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_admin_frag_maintenance_detail, container, false);

        // new instances of sessionManager
        sessionManager = new SessionManager();

        // assign editTexts to variable
        editTextMaintenanceFName = (EditText) view.findViewById(R.id.textMaintenanceFName);
        editTextMaintenanceLName = (EditText) view.findViewById(R.id.textMaintenanceLName);
        editTextMaintenanceAddress = (EditText) view.findViewById(R.id.textMaintenanceAddress);
        editTextMaintenanceCurrentPinCode = (EditText) view.findViewById(R.id.textMaintenanceCurrentPassword);
        editTextMaintenanceNewPinCode = (EditText) view.findViewById(R.id.textMaintenanceNewPassword);

        // assign buttons to variables
        btnProceedMaintenance = (Button) view.findViewById(R.id.btnMaintenanceProceed);
        btnCancelMaintenance = (Button) view.findViewById(R.id.btnMaintenanceCancel);

        // get arguments -> strMaintenanceUserID
        final String strMaintenanceUserID = getArguments().getString("strMaintenanceUserID");

        // execute background task
        new BackgroundTasks(strMaintenanceUserID).execute();

        // btnProceedMaintenance -> onClickListener
        btnProceedMaintenance.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // method call -> onClickProceedMaintenance
                onClickProceedMaintenance(strMaintenanceUserID);

            }// end onClick

        });// end Listener

        // btnCancelMaintenance -> onClickListener
        btnCancelMaintenance.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // method call -> onClickCancelMaintenance
                onClickCancelMaintenance();

            }// end onClick

        });// end Listener

        // linearLayout -> withdraw layout
        LinearLayout linearRoot = (LinearLayout) view.findViewById(R.id.linearMaintenance);

        // for loop -> check for instances of editTexts
        for(int i = 0; i < linearRoot.getChildCount(); i++) {

            if(linearRoot.getChildAt(i) instanceof EditText) {

                editTextList.add((EditText) linearRoot.getChildAt(i));

            }// add instances of editTexts to arraylist

        }// end for loop -> check for instances of editTexts

        // return inflated layout for this fragment
        return view;

    }// end onCreateView

    public void onClickProceedMaintenance(final String strMaintenanceUserID) {

        if (isEmpty()) {

            // toast -> empty fields
            Toast.makeText(context, "Empty fields detected!", Toast.LENGTH_SHORT).show();

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

                                // method call -> updateRecord
                                updateRecord(strMaintenanceUserID);

                                FragmentManager fragmentManager = getFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                AdminMaintenanceList adminMaintenanceList = new AdminMaintenanceList(context);

                                fragmentTransaction.replace(R.id.fragment_container,adminMaintenanceList);
                                fragmentTransaction.commit();

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


    }// end method onClickProceedMaintenance

    public void onClickCancelMaintenance() {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {

                    case DialogInterface.BUTTON_POSITIVE:

                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        AdminMaintenanceList adminMaintenanceList = new AdminMaintenanceList(context);

                        fragmentTransaction.replace(R.id.fragment_container,adminMaintenanceList);
                        fragmentTransaction.commit();

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

    }// end method onClickCancelMaintenance

    public boolean isEmpty() {

        boolean boolIsEmpty = true;

        // for loop -> iterate through editTexts
        for (int intCounter = 0; intCounter < (editTextList.size()); intCounter++) {

            // validate current editText if empty
            boolIsEmpty = Validation.validateEmpty(editTextList.get(intCounter));

        }// end for loop -> iterate through editTexts

        return boolIsEmpty;

    }// end method isEmpty

    public void updateRecord(String strMaintenanceUserID) {

        // content values -> bank account
        ContentValues cvBankAccount = new ContentValues();
        cvBankAccount.put(BankAccount.KEY_BANKACCOUNT_PIN, SecurityManager.encryptIt(editTextMaintenanceNewPinCode.getText().toString().trim()));

        // DatabaseHelper -> update pin
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        dbHelper.update(BankAccount.TABLE,
                        cvBankAccount,
                        BankAccount.KEY_BANKACCOUNT_ID + "= ?",
                        new String[]{String.valueOf(strMaintenanceUserID)});

        // content values -> person
        ContentValues cvPerson = new ContentValues();
        cvPerson.put(Person.KEY_PERSON_FNAME, editTextMaintenanceFName.getText().toString().trim());
        cvPerson.put(Person.KEY_PERSON_LNAME, editTextMaintenanceLName.getText().toString().trim());
        cvPerson.put(Person.KEY_PERSON_ADDRESS, editTextMaintenanceAddress.getText().toString().trim());

        // DatabaseHelper -> update person
        dbHelper.update(Person.TABLE,
                cvPerson,
                Person.KEY_PERSON_ID + "= ?",
                new String[]{String.valueOf(strMaintenanceUserID)});

        // toast -> successfully updated
        Toast.makeText(context, "Record successfully updated!", Toast.LENGTH_SHORT).show();

    }// end method updateRecord

    class BackgroundTasks extends AsyncTask<Void,Void,String> {

        // declarations
        String strMaintenanceUserID;
        String strUserFName;
        String strUserLName;
        String strUserAddress;
        String strUserCurrentPin;
        String strUserNewPin;

        public BackgroundTasks(String strMaintenanceUserID) {

            this.strMaintenanceUserID = strMaintenanceUserID;

        }// end constructor

        @Override
        protected void onPreExecute() {

        }// end onPreExecute

        @Override
        protected String doInBackground(Void... voids) {

            // fetch all activity logs from database
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cUserRecord = db.rawQuery("SELECT p.strPersonFName, p.strPersonLName," +
                    " p.strPersonAddress, b.strBankAccountPassword" +
                    " FROM Person p JOIN BankAccount b ON p.intPersonID" +
                    " = b.intBankAccountID WHERE p.intPersonID = '" + strMaintenanceUserID + "'", null);

            if (cUserRecord.moveToFirst()) {

                do {

                        strUserFName = cUserRecord.getString(0);
                        strUserLName = cUserRecord.getString(1);
                        strUserAddress = cUserRecord.getString(2);
                        strUserCurrentPin = SecurityManager.decryptIt(cUserRecord.getString(3));

                } while (cUserRecord.moveToNext());

            }// end if

            cUserRecord.close();
            db.close();

            return null;

        }// end doInBackground

        @Override
        protected void onProgressUpdate(Void... values) {

            super.onProgressUpdate(values);

        }// end onProgressUpdate

        @Override
        protected void onPostExecute(String result) {

            editTextMaintenanceFName.setText(strUserFName);
            editTextMaintenanceLName.setText(strUserLName);
            editTextMaintenanceAddress.setText(strUserAddress);
            editTextMaintenanceCurrentPinCode.setText(strUserCurrentPin);

        }// end method onPostExecute

    }// end class BackgroundTasks

}// end class AdminMaintenanceDetail