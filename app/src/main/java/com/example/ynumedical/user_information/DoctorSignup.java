package com.example.ynumedical.user_information;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.example.ynumedical.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class DoctorSignup extends AppCompatActivity {

    private FirebaseAuth auth;
    EditText editTextName, editTextEmail, editTextPassword, editTextSpecialty;
    RadioGroup genderRadioGroup;
    Spinner specialtySpinner;
    private boolean isAtLeast6 = false;
    CardView cardOne;
    private ProgressBar progressBar2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_signup);

        progressBar2 = (ProgressBar) findViewById(R.id.progressBar2);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Create An Account");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2E5DA3")));

        String[] specialties = new String[] {"Specialty", "Cardiology", "Dermatology", "Family Medicine", "Neurology"
                , "Gynecology", "Pediatrics", "Physiotherapy", "Psychiatry"};
        Spinner specialtySpinner = (Spinner)findViewById(R.id.specialty_doctor);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, specialties);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        specialtySpinner.setAdapter(adapter);

        editTextPassword = (EditText) findViewById(R.id.doctor_password);
        cardOne = (CardView) findViewById(R.id.doctor_cardOne);

        auth = FirebaseAuth.getInstance();
        Button update_info = findViewById(R.id.add_doctor_info);
        update_info.setAllCaps(false);
        update_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                editTextPassword = (EditText) findViewById(R.id.doctor_password);

                editTextName = (EditText) findViewById(R.id.doctor_name);

                editTextEmail = (EditText) findViewById(R.id.doctor_email);


                genderRadioGroup = (RadioGroup)findViewById(R.id.gender_doctor);

                registerUser();


            }
        });

        inputChange();

    }

    private void registerUser() {


        String newPassword = editTextPassword.getText().toString().trim();

        String newName = editTextName.getText().toString().trim();

        String newEmail = editTextEmail.getText().toString().trim();

        Spinner specialtySpinner = (Spinner)findViewById(R.id.specialty_doctor);
        String newSpecialty = specialtySpinner.getSelectedItem().toString().trim();

        String gender = ((RadioButton)findViewById(genderRadioGroup.getCheckedRadioButtonId()))
                .getText().toString();



        if (newName.isEmpty()) {
            editTextName.setError("Full name is required!");
            editTextName.requestFocus();
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

        if (newSpecialty.equals("Specialty")) {
            new AlertDialog.Builder(DoctorSignup.this)
                    .setTitle("Please select your specialty!")

                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            editTextEmail.setError("Please provide valid email");
            editTextEmail.requestFocus();
            return;
        }


        progressBar2.setVisibility(View.VISIBLE);

        auth.createUserWithEmailAndPassword(newEmail, newPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Doctor newDoctor = new Doctor(newName, newEmail, newPassword, gender, newSpecialty);

                            FirebaseDatabase.getInstance().getReference().child("doctors").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(newDoctor).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){

                                        startActivity(new Intent(DoctorSignup.this, DoctorActivity.class));

                                    } else{
                                        new AlertDialog.Builder(DoctorSignup.this)
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
                            new AlertDialog.Builder(DoctorSignup.this)
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