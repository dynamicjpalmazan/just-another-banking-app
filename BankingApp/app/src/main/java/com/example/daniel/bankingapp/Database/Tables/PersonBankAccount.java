package com.example.daniel.bankingapp.Database.Tables;

/**
 * Created by Daniel on 11/21/2016.
 */
public class PersonBankAccount extends BankAccount{

    //table name
    public static final String TABLE = "PersonBankAccount";

    //table column names
    public static final String KEY_PBA_ID = "strPBAID";
    public static final String KEY_PBA_PERSON_ID = "intPBAPersonID";
    public static final String KEY_PBA_BANKACCOUNT_ID = "intPBABankAccountID";

    //table metadata
    public String PBAID;
    public String PBAPersonID;
    public String PBABankAccountID;

    //** person bank account id
    public void setPersonBankAccountID (String strID) {

        PBAID = strID;

    }// end method setPersonBankAccountID

    public String getPersonBankAccountID () {

        return PBAID;

    }// end method getPersonBankAccountID

}// end class PersonBankAccount
