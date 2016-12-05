package com.example.daniel.bankingapp.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.daniel.bankingapp.BuildConfig;
import com.example.daniel.bankingapp.Database.Tables.ActivityLog;
import com.example.daniel.bankingapp.Database.Tables.BankAccount;
import com.example.daniel.bankingapp.Database.Tables.Person;
import com.example.daniel.bankingapp.Database.Tables.PersonBankAccount;

/**
 * Created by Daniel on 11/21/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // declarations
    static final int DB_VERSION = 1;
    static final String DB_NAME = "bankingdb";
    private final Context mContext;

    public DatabaseHelper(Context mContext) {

        super(mContext, DB_NAME, null, DB_VERSION);
        this.mContext = mContext;

    }// end constructor

    @Override
    public void onCreate(SQLiteDatabase db) {

        // sql statement -> create person table
        String SQLCreatePersonTable = "CREATE TABLE " + Person.TABLE + "("
                + Person.KEY_PERSON_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + Person.KEY_PERSON_FNAME + " TEXT, "
                + Person.KEY_PERSON_LNAME + " TEXT, "
                + Person.KEY_PERSON_ADDRESS + " TEXT )";

        // execute statement -> create person table
        db.execSQL(SQLCreatePersonTable);

        // sql statement -> create bank account table
        String SQLCreateBankAccountTable = "CREATE TABLE " + BankAccount.TABLE + "("
                + BankAccount.KEY_BANKACCOUNT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + BankAccount.KEY_BANKACCOUNT_PIN + " TEXT, "
                + BankAccount.KEY_BANKACCOUNT_BAL + " DOUBLE )";

        // execute statement -> create bank account table
        db.execSQL(SQLCreateBankAccountTable);

        // sql statement -> create person_bank_account table
        String SQLCreatePersonBankAccountTable = "CREATE TABLE " + PersonBankAccount.TABLE + "("
                + PersonBankAccount.KEY_PBA_ID + " TEXT ,"
                + PersonBankAccount.KEY_PBA_PERSON_ID + " INTEGER, "
                + PersonBankAccount.KEY_PBA_BANKACCOUNT_ID + " INTEGER )";

        // execute statement -> create person_bank_account table
        db.execSQL(SQLCreatePersonBankAccountTable);

        // sql statement -> create activity log table
        String SQLCreateActivityLogTable = "CREATE TABLE " + ActivityLog.TABLE + "("
                + ActivityLog.KEY_LOG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + ActivityLog.KEY_LOG_PROCEDURE + " TEXT, "
                + ActivityLog.KEY_LOG_STAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + ActivityLog.KEY_LOG_PERSON_ID + " INTEGER )";

        // execute statement -> create activity log table
        db.execSQL(SQLCreateActivityLogTable);

    }// end method onCreate

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // drop existing activity log table
        db.execSQL("DROP TABLE IF EXISTS " + ActivityLog.TABLE);
        // drop existing person_bank_account table
        db.execSQL("DROP TABLE IF EXISTS " + PersonBankAccount.TABLE);
        // drop existing person table
        db.execSQL("DROP TABLE IF EXISTS " + Person.TABLE);
        // drop existing bank account table
        db.execSQL("DROP TABLE IF EXISTS " + BankAccount.TABLE);

        // create tables again
        onCreate(db);

    }// end method onUpgrade

    public long insert(String table, ContentValues values) {

        SQLiteDatabase mDatabase = getWritableDatabase();
        long longRowID = mDatabase.insert(table, null, values);

        mDatabase.close();

        return longRowID;

    }// end method insert

    public int update(String table, ContentValues values, String whereClause,
                      String[] whereArgs) {

        SQLiteDatabase mDatabase = getWritableDatabase();
        int rows = mDatabase.update(table, values, whereClause, whereArgs);

        mDatabase.close();

        return rows;

    }// end method update

    public int delete(String table, String whereClause, String[] whereArgs) {

        SQLiteDatabase mDatabase = getWritableDatabase();
        int rows = mDatabase.delete(table, whereClause, whereArgs);

        mDatabase.close();

        return rows;

    }// end method delete

}// end class DatabaseHelper

