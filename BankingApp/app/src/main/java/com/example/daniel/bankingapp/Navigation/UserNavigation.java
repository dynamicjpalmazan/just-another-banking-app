package com.example.daniel.bankingapp.Navigation;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.daniel.bankingapp.Database.DatabaseHelper;
import com.example.daniel.bankingapp.Database.Tables.ActivityLog;
import com.example.daniel.bankingapp.Main.MainActivity;
import com.example.daniel.bankingapp.R;
import com.example.daniel.bankingapp.User.UserAboutUs;
import com.example.daniel.bankingapp.User.UserBalance;
import com.example.daniel.bankingapp.User.UserDeposit;
import com.example.daniel.bankingapp.User.UserTransfer;
import com.example.daniel.bankingapp.User.UserWithdraw;
import com.example.daniel.bankingapp.Utility.*;
import com.example.daniel.bankingapp.Utility.SecurityManager;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

public class UserNavigation extends AppCompatActivity
       implements NavigationView.OnNavigationItemSelectedListener {

    // declarations
    // auto-generated indexing variable
    private GoogleApiClient client;

    // navigation view variables
    NavigationView navigationView = null;
    Toolbar toolbar = null;

    // session sessionManager
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_nav_drawer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set initial fragment
        DefaultFragment defaultFragment = new DefaultFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, defaultFragment);
        ft.commit();

        // set listener for drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                                                                 drawer,
                                                                 toolbar,
                                                                 R.string.navigation_drawer_open,
                                                                 R.string.navigation_drawer_close);

        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // set listener for navigation
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // sessionManager object
        sessionManager = new SessionManager();

        // set active user's info in header view
        View hview = navigationView.getHeaderView(0);
        TextView txtName = (TextView)hview.findViewById(R.id.textViewUsername);
        txtName.setText(sessionManager.getPreferences(UserNavigation.this, "UserLName") + ", " +
                        sessionManager.getPreferences(UserNavigation.this, "UserFName"));

        TextView txtAddress = (TextView)hview.findViewById(R.id.textViewAddress);
        txtAddress.setText(sessionManager.getPreferences(UserNavigation.this, "UserAddress"));

        // auto-generated indexing code
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }// end method onCreate

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {

            // if drawer is open, close it
            drawer.closeDrawer(GravityCompat.START);

        } else {

            super.onBackPressed();

            // else, close the app
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
            startActivity(intent);
            finish();
            System.exit(0);

        }// end conditional operations

    }// end method onBackPressed

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_withdraw) {

            //replace current fragment -> UserWithdraw
            UserWithdraw userWithdraw = new UserWithdraw(this);
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment_container, userWithdraw);
            ft.commit();

        } else  if (id == R.id.nav_deposit) {

            //replace current fragment -> UserDeposit
            UserDeposit userDeposit = new UserDeposit(this);
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment_container, userDeposit);
            ft.commit();

        } else if (id == R.id.nav_check_balance) {

            // method call -> checkBalanceDialog
            checkBalanceDialog();

        } else if (id == R.id.nav_money_transfer) {

            // method call -> moneyTransferDialog
            moneyTransferDialog();

        } else if (id == R.id.nav_about_us) {

            //replace current fragment -> UserAboutUS
            UserAboutUs userAbout = new UserAboutUs();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment_container, userAbout);
            ft.commit();

        } else if (id == R.id.nav_logout) {

            // method call -> logoutAccountDialog
            logoutAccountDialog();

        }// end conditional operations

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;

    }// end method onNavigationItemSelected

    @Override
    public void onResume() {

        super.onResume();
        setActionBarTitle(getString(R.string.app_name));

    }// end method onResume

    public void setActionBarTitle(String title) {

        getSupportActionBar().setTitle(title);

    }// end method setActionBarTitle

    public void checkBalanceDialog() {

        // new linearLayout -> vertical
        LinearLayout linearLayoutCheckBalanceDialog = new LinearLayout(this);
        linearLayoutCheckBalanceDialog.setOrientation(LinearLayout.VERTICAL);

        // new layoutParams
        LinearLayout.LayoutParams layoutParamsCheckBalanceDialog = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        // layoutParams -> setMargins
        layoutParamsCheckBalanceDialog.setMargins(100, 0, 100, 0);

        // new final editTextPinCode
        final EditText editTextPinCode = new EditText(this);

        // allow only numbers | password
        editTextPinCode.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        // set maximum length
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(4);
        editTextPinCode.setFilters(FilterArray);

        // set contents of linearLayout
        linearLayoutCheckBalanceDialog
                .addView(editTextPinCode, layoutParamsCheckBalanceDialog);

        new AlertDialog.Builder(this)
                .setTitle("Verify identity")
                .setMessage("Please enter your pin code below:")
                .setView(linearLayoutCheckBalanceDialog)
                .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        String strPinCode = editTextPinCode.getText().toString().trim();

                        if (strPinCode.equals(SecurityManager.decryptIt(sessionManager.getPreferences(UserNavigation.this, "UserPinCode")))) {

                            // replace current fragment -> UserBalance
                            UserBalance userBalance = new UserBalance(UserNavigation.this);
                            FragmentManager fm = getSupportFragmentManager();
                            FragmentTransaction ft = fm.beginTransaction();
                            ft.replace(R.id.fragment_container, userBalance);
                            ft.commit();

                        } else {

                            // toast -> empty fields
                            Toast.makeText(UserNavigation.this, "Invalid pin code!", Toast.LENGTH_SHORT).show();

                        }// end pin validation

                    }// end positive onClick

                })// end set positive button

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        // do something

                    }// end negative onClick

                })// end negative button

                .show();

    }// end method checkBalanceDialog

    public void moneyTransferDialog() {

        // new linearLayout -> vertical
        LinearLayout linearLayoutCheckBalanceDialog = new LinearLayout(this);
        linearLayoutCheckBalanceDialog.setOrientation(LinearLayout.VERTICAL);

        // new layoutParams
        LinearLayout.LayoutParams layoutParamsCheckBalanceDialog = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        // layoutParams -> setMargins
        layoutParamsCheckBalanceDialog.setMargins(100, 0, 100, 0);

        // new final editTextPinCode
        final EditText editTextPinCode = new EditText(this);

        // allow only numbers | password
        editTextPinCode.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        // set maximum length
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(4);
        editTextPinCode.setFilters(FilterArray);

        // set contents of linearLayout
        linearLayoutCheckBalanceDialog
                .addView(editTextPinCode, layoutParamsCheckBalanceDialog);

        new AlertDialog.Builder(this)
                .setTitle("Verify identity")
                .setMessage("Please enter your pin code below:")
                .setView(linearLayoutCheckBalanceDialog)
                .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        String strPinCode = editTextPinCode.getText().toString().trim();

                        if (strPinCode.equals(SecurityManager.decryptIt(sessionManager.getPreferences(UserNavigation.this, "UserPinCode")))) {

                            //replace current fragment -> UserTransfer
                            UserTransfer userTransfer = new UserTransfer(UserNavigation.this);
                            FragmentManager fm = getSupportFragmentManager();
                            FragmentTransaction ft = fm.beginTransaction();
                            ft.replace(R.id.fragment_container, userTransfer);
                            ft.commit();

                        } else {

                            // toast -> empty fields
                            Toast.makeText(UserNavigation.this, "Invalid pin code!", Toast.LENGTH_SHORT).show();

                        }// end pin validation

                    }// end positive onClick

                })// end set positive button

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        // do something

                    }// end negative onClick

                })// end negative button

                .show();

    }// end method moneyTransferDialog

    public void logoutAccountDialog() {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {

                    case DialogInterface.BUTTON_POSITIVE:

                        DatabaseHelper dbHelper = new DatabaseHelper(UserNavigation.this);
                        ContentValues cvActivityLog = new ContentValues();
                        cvActivityLog.put(ActivityLog.KEY_LOG_PERSON_ID, sessionManager.getPreferences(UserNavigation.this, "UserID"));
                        cvActivityLog.put(ActivityLog.KEY_LOG_PROCEDURE, ActivityLog.PROCEDRE_lOGOUT);

                        // DatabaseHelper -> insert
                        dbHelper.insert(ActivityLog.TABLE, cvActivityLog);

                        // revert SessionStatus to default -> zero
                        sessionManager.setPreferences(UserNavigation.this, "SessionStatus", "0");

                        // start activity -> MainActivity
                        Intent intentMain = new Intent(UserNavigation.this, MainActivity.class);
                        intentMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intentMain);

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;

                }// end switch -> which

            }// end DialogInterface -> Logout -> onClickLogin

        };// end DialogInterface

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // set alert dialog's content
        builder.setMessage("Are you sure?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }// end method logoutAccountDialog

}// end class UserNavigation
