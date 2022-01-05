package com.example.ynumedical.appointment_activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.ynumedical.user_information.Doctor;
import com.example.ynumedical.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PatientChooseDoctorActivity extends AppCompatActivity {
    Button newDoctor;
    FirebaseAuth auth;
    Spinner specialtyFilterSpinner;
    Spinner genderFilterSpinner;
    Button refresh;
    LinearLayout doctorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_choose_doctor);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Book Appointments");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2E5DA3")));

        //Declaring Specialties and initializing spinner
        String[] specialties = new String[] {"Any", "Cardiology", "Dermatology", "Family Medicine", "Neurology"
                , "Gynecology", "Pediatrics", "Physiotherapy", "Psychiatry"};
        specialtyFilterSpinner = (Spinner)findViewById(R.id.filter_specialty);
        ArrayAdapter<String> specialtyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, specialties);
        specialtyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        specialtyFilterSpinner.setAdapter(specialtyAdapter);

        //Declaring genders and initializing spinner
        String [] genders = new String [] {"Any", "Male", "Female"};
        genderFilterSpinner = (Spinner) findViewById(R.id.filter_gender);
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genders);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderFilterSpinner.setAdapter(genderAdapter);


        refresh = (Button) findViewById(R.id.filter_refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                doctorList = (LinearLayout) findViewById(R.id.doctor_list);
                doctorList.removeAllViews(); //When new filter is applied, previous results are erased.

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("doctors");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child: dataSnapshot.getChildren()){
                            Doctor d = child.getValue(Doctor.class);
                            addDoctor(d, child.getKey());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.w("info", "Failed to read value.", error.toException());
                    }
                });
            }
        });

    }



    public void addDoctor(Doctor doctor, String id){
        String doctorID = id;
        doctorList = (LinearLayout) findViewById(R.id.doctor_list);

         String filterSpecialty = specialtyFilterSpinner.getSelectedItem().toString().trim();
         String filterGender = genderFilterSpinner.getSelectedItem().toString().trim();

         //Check that doctor matches filters specified by user
         if((doctor.getGender().equals(filterGender) || filterGender.equals("Any")) && (doctor.getSpecialty().equals(filterSpecialty) || filterSpecialty.equals("Any"))){
             newDoctor = new Button(this);
             newDoctor.setText(doctor.name);
             newDoctor.setAllCaps(false);

             newDoctor.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     Intent navigateToAvailabilityIntent = new Intent(PatientChooseDoctorActivity.this, PatientBookAppointmentActivity.class);
                     navigateToAvailabilityIntent.putExtra("doctorID", doctorID);
                     startActivity(navigateToAvailabilityIntent);
                 }
             });
             doctorList.addView(newDoctor);
         }

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