package com.example.ynumedical;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ynumedical.user_information.DoctorActivity;
import com.example.ynumedical.user_information.DoctorSignup;
import com.example.ynumedical.user_information.PatientActivity;
import com.example.ynumedical.user_information.PatientSignup;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginPage extends AppCompatActivity {

    private Presenter presenter;

    private TextInputEditText loginEmail;
    private TextInputEditText loginPassword;
    private Button logIn;
    private FirebaseAuth auth;
    private ProgressBar progressBar;

    public void displayMessage(String message){
        TextView textView = findViewById(R.id.existedUser);
        textView.setText(message);
    }

    public String getEmail(){
        String email = loginEmail.getText().toString().trim();
        //EditText editText = findViewById(R.id.email_text);
        return email;
    }

    public String getPassword(){
        String password = loginPassword.getText().toString().trim();
        //EditText editText = findViewById(R.id.password_text);
        return password;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        Context pageContext = this;
        getSupportActionBar().setTitle("YNU MEDICAL");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2E5DA3")));

        presenter = new Presenter(new Model(), this);

        loginEmail = (TextInputEditText) findViewById(R.id.email_text);
        loginPassword = (TextInputEditText) findViewById(R.id.password_text);

        progressBar = (ProgressBar) findViewById(R.id.login_progressBar);
        auth =FirebaseAuth.getInstance();


        logIn = (Button) findViewById(R.id.login_button);
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                login();


            }
        });


        Button signupPatient = findViewById(R.id.signup_patient);
        signupPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToPatientSignup();
            }
        });

        Button signupDoctor = findViewById(R.id.signup_doctor);
        signupDoctor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginPage.this, DoctorSignup.class));
            }
        });

    }


    public void navigateToPatientActivity(){
        Intent navigateToPatientIntent = new Intent(LoginPage.this, PatientActivity.class);
        //navigateToPatientIntent.putExtra("patient", patient);

        startActivity(navigateToPatientIntent);
    }

    public void navigateToPatientSignup(){
        Intent navigateToPatientSignup = new Intent(this, PatientSignup.class);
        startActivity(navigateToPatientSignup);
    }

    public void login(){
        presenter.login();
    }

    public void userLogin(String email, String password) {


        progressBar.setVisibility(View.VISIBLE);

        Context pageContext = this;

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Get current UID
                    String currentLoggedInUID = auth.getUid();
                    Log.i("UID OBTAINED", currentLoggedInUID);

                    final Boolean[] checkIfDoc = {false};

//                    final ConfirmCallback cb = null;

                    //Match UID to see if user is a doctor... run through doctor UID list
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("doctors");
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot child: snapshot.getChildren()){
                                String curDocUID = child.getKey();
                                if(curDocUID.equals(currentLoggedInUID)){
                                    Log.i("UID MATCHED", curDocUID);
                                    checkIfDoc[0] = true;
                                    startActivity(new Intent(LoginPage.this, DoctorActivity.class));
//                                    confirmUserPageNavigationCallback(true);
                                }
                            }
//                            checkIfDoc[0] = false;

                            if(checkIfDoc[0])
                                startActivity(new Intent(LoginPage.this, DoctorActivity.class));
                            else
                                startActivity(new Intent(LoginPage.this, PatientActivity.class));
//                            if(checkIfDoc[0] != null && checkIfDoc[0] == false)
//                            confirmUserPageNavigationCallback(false);
//                            cb.confirmCallback(checkIfDoc[0]);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            new AlertDialog.Builder(LoginPage.this)
                                    .setTitle("Something went wrong, please restart the application")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Continue with delete operation
                                        }
                                    });
                        }
                    });
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    new AlertDialog.Builder(pageContext)
                            .setTitle("Login information was incorrect, please try again.")
                            //.setMessage("Are you sure you want to delete this entry?")

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with delete operation
                                }
                            });

                }

            }
        });

    }

    public void confirmUserPageNavigationCallback(boolean isDoc) {
        if (isDoc) {
            startActivity(new Intent(LoginPage.this, DoctorActivity.class));
        } else {
            //Else not a doctor and login via patientlogin
            startActivity(new Intent(LoginPage.this, PatientActivity.class));
        }
    }
}

