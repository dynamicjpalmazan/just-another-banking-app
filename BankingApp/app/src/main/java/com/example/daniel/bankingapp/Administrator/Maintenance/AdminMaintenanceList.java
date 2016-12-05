package com.example.daniel.bankingapp.Administrator.Maintenance;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.daniel.bankingapp.Administrator.Maintenance.AdminMaintenanceDetail;
import com.example.daniel.bankingapp.Database.DatabaseHelper;
import com.example.daniel.bankingapp.Database.Tables.BankAccount;
import com.example.daniel.bankingapp.Database.Tables.Person;
import com.example.daniel.bankingapp.Database.Tables.PersonBankAccount;
import com.example.daniel.bankingapp.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Daniel on 11/28/2016.
 */
public class AdminMaintenanceList extends Fragment{

    // declarations
    Context context;

    LinearLayout linearLayout;
    ListView listView;

    ArrayList<HashMap<String, String>> arrayListUserRecord;
    ListAdapter listAdapter;

    public AdminMaintenanceList(Context context) {

        this.context = context;

    }// end constructor

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // execute background task
        new BackgroundTasks().execute();

        linearLayout = (LinearLayout)inflater.inflate(R.layout.activity_admin_frag_maintenance_list, container, false);

        return linearLayout;

    }// end method onCreateView

    public void selectedItemDialog(final String strMaintenanceUserID) {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {

                    case DialogInterface.BUTTON_POSITIVE:

                        // method call -> deleteDialog
                        deleteDialog(strMaintenanceUserID);

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        AdminMaintenanceDetail adminMaintenanceDetail = new AdminMaintenanceDetail(context);

                        Bundle bundle = new Bundle();
                        bundle.putString("strMaintenanceUserID", strMaintenanceUserID);
                        adminMaintenanceDetail.setArguments(bundle);

                        fragmentTransaction.replace(R.id.fragment_container, adminMaintenanceDetail);
                        fragmentTransaction.commit();

                }// end switch -> which

            }// end DialogInterface -> Logout -> onClickLogin

        };// end DialogInterface

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // set alert dialog's content
        builder.setTitle("Account Maintenance")
                .setMessage("What would you like to do?")
                .setNegativeButton("Edit", dialogClickListener)
                .setPositiveButton("Delete", dialogClickListener)
                .show();

    }// end selectedItemDialog

    public void deleteDialog(final String strMaintenanceUserID) {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {

                    case DialogInterface.BUTTON_POSITIVE:

                        // method call -> deleteRecord
                        deleteRecord(strMaintenanceUserID);

                        new BackgroundTasks().execute();

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // no button pressed
                        break;

                }// end switch -> which

            }// end DialogInterface -> Logout -> onClickLogin

        };// end DialogInterface

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // set alert dialog's content
        builder.setMessage("Are you sure?")
                .setNegativeButton("No", dialogClickListener)
                .setPositiveButton("Yes", dialogClickListener)
                .show();

    }// end method deleteDialog

    public void deleteRecord(String strMaintenanceUserID) {

        DatabaseHelper dbHelper = new DatabaseHelper(context);

        // DatabaseHelper -> delete from person_bank_account table;
        dbHelper.delete(PersonBankAccount.TABLE,
                        PersonBankAccount.KEY_PBA_BANKACCOUNT_ID + "= ?",
                        new String[]{String.valueOf(strMaintenanceUserID)});

        // DatabaseHelper -> delete from bank account table
        dbHelper.delete(BankAccount.TABLE,
                        BankAccount.KEY_BANKACCOUNT_ID + "= ?",
                        new String[]{String.valueOf(strMaintenanceUserID)});

        // DatabaseHelper -> delete from person table
        dbHelper.delete(Person.TABLE,
                        Person.KEY_PERSON_ID + "= ?",
                        new String[]{String.valueOf(strMaintenanceUserID)});


        // toast -> successfully deleted
        Toast.makeText(context, "Record successfully deleted!", Toast.LENGTH_SHORT).show();

    }// end method deleteRecord

    class BackgroundTasks extends AsyncTask<Void,Void,String> {

        @Override
        protected void onPreExecute() {

            // create a new HashMap
            arrayListUserRecord = new ArrayList<>();

        }// end onPreExecute

        @Override
        protected String doInBackground(Void... voids) {

            // fetch all activity logs from database
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cUserRecord = db.rawQuery("SELECT p.intPersonID, p.strPersonFName, p.strPersonLName\n" +
                    " FROM Person p ORDER BY p.strPersonLName", null);

            if (cUserRecord.moveToFirst()) {

                do {

                    HashMap<String, String> hashMapUserRecord = new HashMap<>();

                    hashMapUserRecord.put("hmUserID"  , cUserRecord.getString(0));
                    hashMapUserRecord.put("hmUserName", cUserRecord.getString(2) + ", " + cUserRecord.getString(1));

                    arrayListUserRecord.add(hashMapUserRecord);

                } while (cUserRecord.moveToNext());

            }// end if

            cUserRecord.close();
            db.close();

            return null;

        }// end doInBackground

        @Override
        protected void onProgressUpdate(Void... values) {

            super.onProgressUpdate(values);

        }// end onProgressUpdate

        @Override
        protected void onPostExecute(String result) {

            if (arrayListUserRecord.size() == 0) {

                // toast -> empty fields
                Toast.makeText(context, "No records found!", Toast.LENGTH_SHORT).show();

            } else {

                listView = (ListView) linearLayout.findViewById(R.id.listView);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,int position, long id) {

                        // get strMaintenanceUserID of selected item
                        TextView textViewMaintenanceUserID = (TextView) view.findViewById(R.id.textMaintenanceUserID);
                        String strMaintenanceUserID = textViewMaintenanceUserID.getText().toString();

                        // method call -> selectedItemDialog
                        selectedItemDialog(strMaintenanceUserID);

                    }// end listView -> onItemClick

                });// end listView -> setOnClickListener

                // set listAdapter to listView
                listAdapter = new SimpleAdapter(
                        context, arrayListUserRecord,
                        R.layout.generic_list_item,
                        new String[] { "hmUserID", "hmUserName" },
                        new int[] { R.id.textMaintenanceUserID, R.id.textMaintenanceUserName });

                listView.setAdapter(listAdapter);

            }// check if there are existing user records

        }// end method onPostExecute

    }// end class BackgroundTasks

}// end class AdminMaintenanceList
