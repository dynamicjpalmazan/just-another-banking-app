package com.example.daniel.bankingapp.Utility;

import android.text.TextUtils;
import android.widget.EditText;

import com.example.daniel.bankingapp.R;

/**
 * Created by Daniel on 11/22/2016.
 */
public class Validation {

    // method validateEmpty
    public static boolean validateEmpty(EditText editText) {

        // get id of editText
        int intEditTextId = editText.getId();

        // retrieve input from editText
        String strEditTextValue = editText.getText().toString().trim();

        // validate input -> empty fields
        if(TextUtils.isEmpty(strEditTextValue)) {

            if (intEditTextId == R.id.textSignUpFName) {

                editText.setError("Please enter your first name.");
                return true;

            } else if (intEditTextId == R.id.textSignUpLName) {

                editText.setError("Please enter your last name.");
                return true;

            } else if (intEditTextId == R.id.textSignUpAddress) {

                editText.setError("Please enter your address.");
                return true;

            } else if (intEditTextId == R.id.textSignUpInitialDeposit) {

                editText.setError("Please enter your initial deposit.");
                return true;

            } else if (intEditTextId == R.id.textSignUpPassword) {

                editText.setError("Please enter your pin code.");
                return true;

            }  else if (intEditTextId == R.id.textLoginAccountNo) {

                editText.setError("Please enter your account no.");
                return true;

            }  else if (intEditTextId == R.id.textLoginPinCode) {

                editText.setError("Please enter your pin code.");
                return true;

            } else if (intEditTextId == R.id.textWithdrawAmount) {

                editText.setError("Please enter your desired amount.");
                return true;

            } else if (intEditTextId == R.id.textDepositAmount) {

                editText.setError("Please enter your desired amount.");
                return true;

            } else if (intEditTextId == R.id.textTransferReceiverAccount) {

                editText.setError("Please enter the receiver's account no.");
                return true;

            } else if (intEditTextId == R.id.textTransferAmount) {

                editText.setError("Please enter the amount to be transferred.");
                return true;

            } else if (intEditTextId == R.id.textWithdrawAccountNo) {

                editText.setError("Please enter an account no.");
                return true;

            } else if (intEditTextId == R.id.textDepositAccountNo) {

                editText.setError("Please enter an account no.");
                return true;

            } else if (intEditTextId == R.id.textBalanceAccountNo) {

                editText.setError("Please enter an account no.");
                return true;

            } else if (intEditTextId == R.id.textTransferSenderAccountNo) {

                editText.setError("Please enter the sender's account no.");
                return true;

            } else if (intEditTextId == R.id.textTransferReceiverAccountNo) {

                editText.setError("Please enter the receiver's account no.");
                return true;

            } else if (intEditTextId == R.id.textMaintenanceFName) {

                editText.setError("Please enter a name.");
                return true;

            } else if (intEditTextId == R.id.textMaintenanceLName) {

                editText.setError("Please enter a name.");
                return true;

            } else if (intEditTextId == R.id.textMaintenanceAddress) {

                editText.setError("Please enter an address.");
                return true;

            } else if (intEditTextId == R.id.textMaintenanceCurrentPassword) {

                editText.setError("Please enter the account's current pin code.");
                return true;

            } else if (intEditTextId == R.id.textMaintenanceNewPassword) {

                editText.setError("Please enter a new pin code.");
                return true;

            } else if (intEditTextId == R.id.textNewRecordFName) {

                editText.setError("Please enter your first name.");
                return true;

            } else if (intEditTextId == R.id.textNewRecordLName) {

                editText.setError("Please enter your last name.");
                return true;

            } else if (intEditTextId == R.id.textNewRecordAddress) {

                editText.setError("Please enter your address.");
                return true;

            } else if (intEditTextId == R.id.textNewRecordInitialDeposit) {

                editText.setError("Please enter your initial deposit.");
                return true;

            } else if (intEditTextId == R.id.textNewRecordPassword) {

                editText.setError("Please enter your pin code.");
                return true;

            }// end conditional operations

        }// end validate input -> empty fields

        return false;

    }// end method validateEmpty

    // method validateNegative
    public static boolean validateNegative(EditText editText) {

        // retrieve input from editText
        String strEditTextValue = editText.getText().toString().trim();

        if (strEditTextValue.contains("-")) {

            return true;

        }// end validate input -> negative amount

        return false;

    }// end method validateNegative

    // method validateAmount
    public static boolean validateAmount(EditText editText) {

        // get id of editText
        int intEditTextId = editText.getId();

        // retrieve input from editText
        Double dblEditTextValue = Double.parseDouble(editText.getText().toString().trim());

        // minimum deposit of Php200 as per BPI easy saver's guidelines
        if (dblEditTextValue < 200) {

            if (intEditTextId == R.id.textSignUpInitialDeposit) {

                editText.setError("Enter an initial deposit not lower than Php 200.00");
                return true;

            } else if (intEditTextId == R.id.textDepositAmount) {

                editText.setError("Enter an amount not lower than Php 200.00");
                return true;

            } else if (intEditTextId == R.id.textWithdrawAmount) {

                editText.setError("Enter an amount not lower than Php 200.00");
                return true;

            } // end conditional operations

          // maximum deposit per transaction of Php10,000
        } else if (dblEditTextValue > 10000) {

            if (intEditTextId == R.id.textSignUpInitialDeposit) {

                editText.setError("Enter an initial deposit not higher than Php 10,000.00");
                return true;

            } else if (intEditTextId == R.id.textDepositAmount) {

                editText.setError("Enter an amount not higher than Php 10,000.00");
                return true;

            } else if (intEditTextId == R.id.textWithdrawAmount) {

                editText.setError("Enter an amount not higher than Php 10,000.00");
                return true;

            } // end conditional operations

        }// end validate input -> valid deposit

        return false;

    }// end method validateAmount

}// end class validation
