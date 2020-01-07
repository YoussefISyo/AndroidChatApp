package com.example.user.androidchatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;


import com.example.user.androidchatapp.R;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.QBSession;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

public class MainActivity extends AppCompatActivity {

    static final String APP_ID = "79545";
    static final String AUTH_KEY = "n7v5fCR7dhCumkp";
    static final String AUTH_SECRET = "Gc7vU5Aatwr8aXN";
    static final String ACCOUNT_KEY = "x7pxqTBzHxMX6UhNcW63";


    TextView textSwitch, switchBtn;
    EditText nameEdit, emailEdit, passwordEdit, emailLogin, passwordLogin;
    ViewSwitcher viewSwitcher;
    LinearLayout signup,login;

    static final int REQUEST_CODE = 1000;

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        requestRuntimePermission();

        initializeFramework();
        registerSession();

        //________________ Email & password Authentication _________________________________________

        textSwitch = findViewById(R.id.textSwitch);
        switchBtn = findViewById(R.id.switchBtn);

        viewSwitcher = findViewById(R.id.viewSwitcher);

        signup = findViewById(R.id.signup);
        login = findViewById(R.id.login);



        emailLogin = findViewById(R.id.emailLoginEdt);
        passwordLogin = findViewById(R.id.passwordLoginEdt);

        nameEdit = findViewById(R.id.nameEdit);
        emailEdit = findViewById(R.id.emailEdit);
        passwordEdit = findViewById(R.id.passwordEdit);



        switchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewSwitcher.getCurrentView()==signup){
                    viewSwitcher.showNext();
                    textSwitch.setText("You don't have an account ?");
                    switchBtn.setText("Sign up");
                }
                else {
                    viewSwitcher.showPrevious();
                    textSwitch.setText("Already have have an account ?");
                    switchBtn.setText("Login");
                }
            }
        });

        progressDialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);

        findViewById(R.id.continueBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewSwitcher.getCurrentView()==signup){

                    QBUser qbUser = new QBUser(emailEdit.getText().toString(),passwordEdit.getText().toString());

                    qbUser.setFullName(nameEdit.getText().toString());

                    QBUsers.signUp(qbUser).performAsync(new QBEntityCallback<QBUser>() {
                        @Override
                        public void onSuccess(QBUser qbUser, Bundle bundle) {
                            Toast.makeText(MainActivity.this, "Signup Successfuly", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(QBResponseException e) {
                            Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }else{

                    QBUser qbUser = new QBUser(emailLogin.getText().toString(),passwordLogin.getText().toString());

                    QBUsers.signIn(qbUser).performAsync(new QBEntityCallback<QBUser>() {
                        @Override
                        public void onSuccess(QBUser qbUser, Bundle bundle) {
                            Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                            Intent mIntent = new Intent(MainActivity.this,ChatDialogsActivity.class);
                            mIntent.putExtra("user",emailLogin.getText().toString());
                            mIntent.putExtra("password",passwordLogin.getText().toString());
                            startActivity(mIntent);
                            finish();
                        }

                        @Override
                        public void onError(QBResponseException e) {

                        }
                    });
                }
            }
        });



    }

    private void requestRuntimePermission() {

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            },REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){

            case REQUEST_CODE:
            {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    private void registerSession() {
        QBAuth.createSession().performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {
            }

            @Override
            public void onError(QBResponseException e) {
                Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeFramework() {
        QBSettings.getInstance().init(getApplicationContext(),APP_ID,AUTH_KEY,AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);
    }


}
