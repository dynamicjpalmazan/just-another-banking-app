package com.example.daniel.bankingapp.Administrator;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.example.daniel.bankingapp.Database.DatabaseHelper;
import com.example.daniel.bankingapp.Navigation.AdministratorNavigation;
import com.example.daniel.bankingapp.R;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Daniel on 11/27/2016.
 */
public class AdminLogs extends Fragment{

    // declarations
    LinearLayout linearLayout;
    ArrayList<String> arrayListActivityLog = new ArrayList<>();
    ListView listView;
    Context context;

    public AdminLogs(Context context) {

        this.context = context;

    }// end constructor

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // execute background task
        new BackgroundTasks().execute();

        linearLayout = (LinearLayout)inflater.inflate(R.layout.activity_admin_frag_logs, container, false);

        return linearLayout;

    }// end method onCreateView

    class BackgroundTasks extends AsyncTask<Void,Void,String> {

        @Override
        protected void onPreExecute() {

        }// end onPreExecute

        @Override
        protected String doInBackground(Void... voids) {

            // fetch all activity logs from database
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cActivityLog = db.rawQuery("SELECT p.intPersonID, p.strPersonFName, p.strPersonLName,\n" +
                    "\t   l.strActivityLogProcedure, datetime(l.dtsCreatedAt, 'localtime') \n" +
                    "FROM   Person p JOIN   ActivityLog l\n" +
                    "\tON p.intPersonID = l.intActivityLogPersonID", null);

            if (cActivityLog.moveToFirst()) {

                do {

                    arrayListActivityLog.add(cActivityLog.getString(2) + ", " +
                                             cActivityLog.getString(1) + " " +
                                             cActivityLog.getString(3) + " on " +
                                             cActivityLog.getString(4));

                } while (cActivityLog.moveToNext());

            }// end if

            cActivityLog.close();
            db.close();

            return null;

        }// end doInBackground

        @Override
        protected void onProgressUpdate(Void... values) {

            super.onProgressUpdate(values);

        }// end onProgressUpdate

        @Override
        protected void onPostExecute(String result) {

                ArrayAdapter<String> adapterActivityLog = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_list_item_1,
                        arrayListActivityLog );

                listView = (ListView) linearLayout.findViewById(R.id.listView);
                listView.setAdapter(adapterActivityLog);

        }// end method onPostExecute

    }// end class BackgroundTasks

    @Override
    public void onResume() {

        super.onResume();
        // Set title
        ((AdministratorNavigation) getActivity()).setActionBarTitle(getString(R.string.activityLogs));

    }// end method onResume

}// end class AdminLogs
