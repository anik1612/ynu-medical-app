package com.example.ynumedical.user_information;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ynumedical.MainActivity;
import com.example.ynumedical.R;
import com.example.ynumedical.appointment_activities.PatientChooseDoctorActivity;
import com.example.ynumedical.appointment_activities.PatientViewPastAppointmentActivity;
import com.example.ynumedical.appointment_activities.PatientViewAppointmentActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class PatientActivity extends AppCompatActivity {

    private FirebaseUser patient;
    private DatabaseReference ref;
    private String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);

        getSupportActionBar().setTitle("My Account");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2E5DA3")));

        MainActivity.setActivityBackgroundColor(0xf0bccbe8, this);

        patient = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference().child("patients");
        userID = patient.getUid();

        final TextView welcome = (TextView) findViewById(R.id.patient_welcome);

        ref.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Patient patientProfile = snapshot.getValue(Patient.class);

                if(patientProfile != null){
                    String name = patientProfile.name;
                    String email = patientProfile.email;
                    Date DOB = patientProfile.getHealthInformation().getDateOfBirth();
                    int weight = patientProfile.getHealthInformation().getWeight();
                    String gender = patientProfile.getHealthInformation().getGender();

                    welcome.setText("Welcome " + name + "!");



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                new AlertDialog.Builder(PatientActivity.this)
                        .setTitle("Something went wrong, please restart the application")

                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                            }
                        });
            }
        });

        Button logout = (Button) findViewById(R.id.signOut);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                FirebaseAuth.getInstance().signOut();
                Intent navigateToPatientIntent = new Intent(PatientActivity.this, MainActivity.class);
                startActivity(navigateToPatientIntent);
            }
        });


    }

    public void navigateToBookAppointmentActivity(View view){
        Intent navigateToPatientIntent = new Intent(this, PatientChooseDoctorActivity.class);
        startActivity(navigateToPatientIntent);
    }

    public void navigateToViewAppointmentsActivity(View view){
        Intent navigateToPatientIntent = new Intent(this, PatientViewAppointmentActivity.class);
        startActivity(navigateToPatientIntent);
    }

    public void navigateToViewPastAppointmentsActivity(View view){
        Intent navigateToPatientIntent = new Intent(this, PatientViewPastAppointmentActivity.class);
        startActivity(navigateToPatientIntent);
    }

    public void navigateToViewVisitedDoctorsActivity(View view){
        Intent navigateToPatientIntent = new Intent(this, PatientViewVisitedDoctorsActivity.class);
        startActivity(navigateToPatientIntent);
    }

}