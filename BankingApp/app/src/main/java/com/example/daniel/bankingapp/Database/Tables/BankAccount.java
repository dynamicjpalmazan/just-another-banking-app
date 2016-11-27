package com.example.daniel.bankingapp.Database.Tables;

/**
 * Created by Daniel on 11/21/2016.
 */
public class BankAccount {

    //table name
    public static final String TABLE = "BankAccount";

    //table column names
    public static final String KEY_BANKACCOUNT_ID = "intBankAccountID";
    public static final String KEY_BANKACCOUNT_PIN = "strBankAccountPassword";
    public static final String KEY_BANKACCOUNT_BAL = "dblBankAccountBalance";

    //table metadata
    public int BankAccountID;
    public String BankAccountPinCode;
    public Double BankAccountBalance;

    //** bank account id
    public void setBankAccountID (int intID) {

        BankAccountID = intID;

    }// end method setBankAccountID

    public int getBankAccountID () {

        return BankAccountID;

    }// end method getBankAccountID

    //** bank account pin
    public void setBankAccountPin (String strPinCode) {

        BankAccountPinCode = strPinCode;

    }// end setBankAccountPin method

    public String getBankAccountPin() {

        return BankAccountPinCode;

    }// end method getBankAccountPin

    //** bank account balance
    public void setBankAccountBalance (Double dblBalance) {

        BankAccountBalance = dblBalance;

    }// end method setBankAccountBalance

    public Double getBankAccountBalance() {

        return BankAccountBalance;

    }// end method getBankAccountBalance

}// end class BankAccount
