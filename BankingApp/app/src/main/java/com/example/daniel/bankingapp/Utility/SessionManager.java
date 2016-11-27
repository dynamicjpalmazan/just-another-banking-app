package com.example.daniel.bankingapp.Utility;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Daniel on 11/23/2016.
 */
public class SessionManager {

    public void setPreferences(Context context, String key, String value) {

        SharedPreferences.Editor editor = context.getSharedPreferences("JustAnotherBankingApp", Context.MODE_PRIVATE).edit();
        editor.putString(key, value);

        editor.commit();

    }// end method setPreferences

    public  String getPreferences(Context context, String key) {

        SharedPreferences prefs = context.getSharedPreferences("JustAnotherBankingApp", Context.MODE_PRIVATE);
        String position = prefs.getString(key, "");

        return position;

    }// end method getPreferences

}// end class SessionManager
