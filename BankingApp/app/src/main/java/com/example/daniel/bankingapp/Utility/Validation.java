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

    }// method validateNegative

}// end class validation
