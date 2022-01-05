package com.example.ynumedical.user_information;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.ynumedical.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PatientSignup extends AppCompatActivity {

    private FirebaseAuth auth;
    EditText editTextName, editTextEmail, editTextPassword, editTextDOB, editTextWeight, editTextBloodType;
    CardView cardOne;
    RadioGroup genderRadioGroup;
    RadioButton genderRadioChoice;
    private ProgressBar progressBar;
    private boolean isAtLeast6 = false;
    private ProgressBar progressBar3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_signup);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        progressBar3 = (ProgressBar) findViewById(R.id.progressBar3);

        getSupportActionBar().setTitle("Create An Account");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2E5DA3")));



        auth = FirebaseAuth.getInstance();

        editTextPassword = (EditText) findViewById(R.id.patient_password);

        cardOne = (CardView) findViewById(R.id.patient_cardOne);

        Button update_info = findViewById(R.id.add_patient_info);
        update_info.setAllCaps(false);
        update_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                editTextPassword = (EditText) findViewById(R.id.patient_password);

                editTextName = (EditText) findViewById(R.id.patient_name);

                editTextEmail = (EditText) findViewById(R.id.patient_email);

                editTextDOB = (EditText) findViewById(R.id.patient_DOB);

                editTextWeight = (EditText) findViewById(R.id.patient_weight);

                genderRadioGroup = (RadioGroup)findViewById(R.id.gender_patient);


                registerUser();


            }
        });
        inputChange();

    }

    public void navigateToPatientActivity(Patient patient){
        Intent navigateToPatientIntent = new Intent(this, PatientActivity.class);
        //navigateToPatientIntent.putExtra("patient", patient);

        startActivity(navigateToPatientIntent);
    }


    private void registerUser() {
        Context pageContext = this;


        String newPassword = editTextPassword.getText().toString().trim();

        String newName = editTextName.getText().toString().trim();

        String newEmail = editTextEmail.getText().toString().trim();

        String newDateOfBirth = editTextDOB.getText().toString();


        String newWeight = editTextWeight.getText().toString();

        String gender = ((RadioButton)findViewById(genderRadioGroup.getCheckedRadioButtonId()))
                .getText().toString();


        if (newName.isEmpty()) {
            editTextName.setError("Full name is required!");
            editTextName.requestFocus();
            return;
        }

        if (newDateOfBirth.isEmpty()) {
            editTextDOB.setError("Date of Birth is required!");
            editTextDOB.requestFocus();
            return;
        }



        Pattern DOBpattern = Pattern.compile("^(19|20)\\d\\d[- /.](0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])$");
        Matcher DOBmatcher = DOBpattern.matcher(newDateOfBirth);
        if (!DOBmatcher.matches()){
            editTextDOB.setError("Invalid Date of Birth");
            editTextDOB.requestFocus();
            return;
        }


        if (newEmail.isEmpty()) {
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
            return;
        }

        if (newPassword.isEmpty()) {
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return;
        }

        if (newPassword.length() < 6) {
            editTextPassword.setError("Password should be at least 6 characters!");
            editTextPassword.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            editTextEmail.setError("Please provide valid email");
            editTextEmail.requestFocus();
            return;
        }

        progressBar3.setVisibility(View.VISIBLE);

        Log.i("USERCREATION", "Creating user with new email and password");
        auth.createUserWithEmailAndPassword(newEmail, newPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Date newDOB = null;
                            try {
                                newDOB = new SimpleDateFormat("yyyy/MM/dd").parse(newDateOfBirth);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            HealthInformation newHealthInformation = new HealthInformation(newDOB, Integer.parseInt(newWeight), gender);
                            Patient newPatient = new Patient(newName, newEmail, newPassword, newHealthInformation);

                            FirebaseDatabase.getInstance().getReference().child("patients").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(newPatient).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
//                                        Toast toast = Toast.makeText(PatientInformation.this, "User successfully registered!", Toast.LENGTH_LONG);
//                                        toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
//                                        toast.show();

                                        navigateToPatientActivity();

                                    } else{
                                        new AlertDialog.Builder(PatientSignup.this)
                                                .setTitle("User failed to register, please try again")
                                                .setMessage("Email may have already been used to create an account")

                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                    }
                                                }).show();
                                    }
                                }
                            });

                        } else {
                            new AlertDialog.Builder(PatientSignup.this)
                                    .setTitle("User failed to register, please try again")
                                    .setMessage("Email may have already been used to create an account")

                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    }).show();
                        }
                    }
                });

    }

    @SuppressLint("ResourceType")
    public void passwordCheck(){
        String password = editTextPassword.getText().toString().trim();
        if (password.length() >= 6){
            isAtLeast6 = true;
            cardOne.setCardBackgroundColor(Color.parseColor(getString(R.color.colorAccent)));
        }
        else{
            isAtLeast6 = false;
            cardOne.setCardBackgroundColor(Color.parseColor(getString(R.color.colorDefault)));
        }
    }

    public void inputChange(){
        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordCheck();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void navigateToPatientActivity(){
        Intent navigateToPatientIntent = new Intent(PatientSignup.this, PatientActivity.class);
        //navigateToPatientIntent.putExtra("patient", patient);

        startActivity(navigateToPatientIntent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}