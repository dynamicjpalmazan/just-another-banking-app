package com.example.daniel.bankingapp.Database.Tables;

/**
 * Created by Daniel on 11/27/2016.
 */
public class ActivityLog {

    // table name
    public static final String TABLE = "ActivityLog";

    // table column names
    public static final String KEY_LOG_ID = "intActivityLogID";
    public static final String KEY_LOG_PROCEDURE = "strActivityLogProcedure";
    public static final String KEY_LOG_STAMP = "dtsCreatedAt";
    public static final String KEY_LOG_PERSON_ID = "intActivityLogPersonID";

    // procedure values
    public static final String PROCEDRE_SIGNUP = "signed-up";
    public static final String PROCEDRE_LOGIN = "logged-in";
    public static final String PROCEDRE_lOGOUT = "logged-out";
    public static final String PROCEDRE_WITHDRAW = "withdrew";
    public static final String PROCEDRE_DEPOSIT = "deposited";
    public static final String PROCEDRE_BALANCE = "checked balance";
    public static final String PROCEDRE_TRANSFER = "transferred";

    // get-set variables
    public int ActivityLogID;
    public String ActivityLogProcedure;
    public String ActivityLogTimestamp;
    public String ActivityLogPersonID;

}// end class ActivityLog