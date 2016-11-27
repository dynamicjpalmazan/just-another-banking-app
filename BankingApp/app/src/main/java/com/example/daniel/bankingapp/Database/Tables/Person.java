package com.example.daniel.bankingapp.Database.Tables;

/**
 * Created by Daniel on 11/21/2016.
 */
public class Person {

    // table name
    public static final String TABLE = "Person";

    // table column names
    public static final String KEY_PERSON_ID = "intPersonID";
    public static final String KEY_PERSON_FNAME = "strPersonFName";
    public static final String KEY_PERSON_LNAME = "strPersonLName";
    public static final String KEY_PERSON_ADDRESS = "strPersonAddress";

    // get-set variables
    public int PersonID;
    public String PersonFName;
    public String PersonLName;
    public String PersonAddress;

    //** person id
    public void setPersonID (int intID) {

        PersonID = intID;

    }// end method setPersonID

    public int getPersonID () {

        return PersonID;

    }// end method getPersonID

    //** person first name
    public void setPersonFName (String strFName) {

        PersonFName = strFName;

    }// end setPersonFName method

    public String getPersonFName() {

        return PersonFName;

    }// end method getPersonLName

    //** person last name
    public void setPersonLName (String strLName) {

        PersonLName = strLName;

    }// end method setPersonFName

    public String getPersonLName() {

        return PersonLName;

    }// end method getPersonLName

    //** person address
    public void setPersonAddress (String strAddress) {

        PersonAddress = strAddress;

    }// end setPersonAddress method

    public String getPersonAddress() {

        return PersonAddress;

    }// end method getPersonAddress

}// end class Person