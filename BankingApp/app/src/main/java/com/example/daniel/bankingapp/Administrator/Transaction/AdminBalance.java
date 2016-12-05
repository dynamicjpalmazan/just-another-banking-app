package com.example.daniel.bankingapp.Administrator.Transaction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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
import com.example.daniel.bankingapp.Main.MainActivity;
import com.example.daniel.bankingapp.Navigation.AdministratorNavigation;
import com.example.daniel.bankingapp.R;
import com.example.daniel.bankingapp.Utility.*;
import com.example.daniel.bankingapp.Utility.SecurityManager;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Daniel on 11/27/2016.
 */
public class AdminBalance extends Fragment{

    // declarations
    Context context;

    // sessionManager
    SessionManager sessionManager;

    // textViews -> balance form
    EditText editTextAccountNo;

    // buttons -> balance form
    Button btnBalanceProceed;
    Button btnBalanceCancel;

    // arrayList -> arrayList of EditTexts
    ArrayList<EditText> editTextList = new ArrayList<EditText>();

    DecimalFormat decFormat = new DecimalFormat("0.00");

    public AdminBalance(Context context) {

        this.context = context;

    }// end constructor

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_admin_frag_balance, container, false);

        // new instances of sessionManager & bankAccount
        sessionManager = new SessionManager();

        // assign textViews to variables
        editTextAccountNo = (EditText) view.findViewById(R.id.textBalanceAccountNo);

        // assign buttons to variables
        btnBalanceProceed = (Button) view.findViewById(R.id.btnBalanceProceed);
        btnBalanceCancel = (Button) view.findViewById(R.id.btnBalanceCancel);

        // btnBalanceProceed -> onClickListener
        btnBalanceProceed.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // method call -> onClickProceedBalance
                onClickProceedBalance();

            }// end onClick

        });// end Listener

        // btnBalanceCancel -> onClickListener
        btnBalanceCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // method call -> onClickCancelBalance
                onClickCancelBalance();

            }// end onClick

        });// end Listener

        // linearLayout -> withdraw layout
        LinearLayout linearRoot = (LinearLayout) view.findViewById(R.id.linearAdminBalance);

        // for loop -> check for instances of editTexts
        for(int i = 0; i < linearRoot.getChildCount(); i++) {

            if(linearRoot.getChildAt(i) instanceof EditText) {

                editTextList.add((EditText) linearRoot.getChildAt(i));

            }// add instances of editTexts to arraylist

        }// end for loop -> check for instances of editTexts

        // return inflated layout for this fragment
        return view;

    }// end onCreateView

    public void onClickProceedBalance() {

        if (isEmpty()) {

            // toast -> empty fields
            Toast.makeText(context, "Empty fields detected!", Toast.LENGTH_SHORT).show();

        } else {

            // new linearLayout -> vertical
            LinearLayout linearLayoutProceedBalanceDialog = new LinearLayout(context);
            linearLayoutProceedBalanceDialog.setOrientation(LinearLayout.VERTICAL);

            // new layoutParams
            LinearLayout.LayoutParams layoutParamsProceedBalanceDialog = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            // layoutParams -> setMargins
            layoutParamsProceedBalanceDialog.setMargins(100, 0, 100, 0);

            // new final editTextPinCode
            final EditText editTextPinCode = new EditText(context);

            // allow only numbers | password
            editTextPinCode.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

            // set maximum length
            InputFilter[] FilterArray = new InputFilter[1];
            FilterArray[0] = new InputFilter.LengthFilter(4);
            editTextPinCode.setFilters(FilterArray);

            // set contents of linearLayout
            linearLayoutProceedBalanceDialog
                    .addView(editTextPinCode, layoutParamsProceedBalanceDialog);

            new AlertDialog.Builder(context)
                    .setTitle("Verify identity")
                    .setMessage("Please enter your pin code below:")
                    .setView(linearLayoutProceedBalanceDialog)
                    .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {

                            String strPinCode = editTextPinCode.getText().toString().trim();

                            if (strPinCode.equals(SecurityManager.decryptIt(sessionManager.getPreferences(context, "UserPinCode")))) {

                                // initialize necessary variables for validations and transactions
                                String strAccountNo = editTextAccountNo.getText().toString().trim();
                                int intBankAccountID = getBankAccountID(strAccountNo);

                                if (intBankAccountID == 0) {

                                    // toast -> non-existent account no.
                                    Toast.makeText(context, "Account no. doesn't exist!", Toast.LENGTH_SHORT).show();

                                } else {

                                    // proceed with check balance
                                    Double dblCurrentBalance = getCurrentBalance(intBankAccountID);

                                    // method call -> checkBalanceDialog
                                    checkBalanceDialog(strAccountNo, dblCurrentBalance);

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

    }// end method onClickProceedBalance

    public void onClickCancelBalance() {

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

    }// end method onClickCancelBalance

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

    public void checkBalanceDialog(String strAccountNo, Double dblCurrentBalance) {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {

                    case DialogInterface.BUTTON_NEUTRAL:

                        // clear fields
                        editTextAccountNo.setText(null);

                        break;

                }// end switch -> which

            }// end DialogInterface -> Logout -> onClickLogin

        };// end DialogInterface

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // set alert dialog's content
        builder.setTitle("Check Balance")
               .setMessage("The current balance of the account \n" +
                           "(#" + strAccountNo + ") is: Php " + decFormat.format(dblCurrentBalance) + ".\n\n")
                .setNeutralButton("Okay", dialogClickListener).show();

    }// end method checkBalanceDialog

    public boolean isEmpty() {

        boolean boolIsEmpty = true;

        // for loop -> iterate through editTexts
        for (int intCounter = 0; intCounter < (editTextList.size()); intCounter++) {

            // validate current editText if empty
            boolIsEmpty = Validation.validateEmpty(editTextList.get(intCounter));

        }// end for loop -> iterate through editTexts

        return boolIsEmpty;

    }// end method isEmpty

}// end class AdminBalance