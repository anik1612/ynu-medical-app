package com.example.ynumedical.appointment_activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ynumedical.R;
import com.example.ynumedical.user_information.Doctor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class PatientViewPastAppointmentActivity extends AppCompatActivity {

    FirebaseAuth auth;
    TextView appointmentInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_view_past_appointment);

        auth = FirebaseAuth.getInstance();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("See Past Appointments");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2E5DA3")));

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot appointmentSnapshot = snapshot.child("Appointments");
                String patientID = auth.getUid();

                ArrayList<Date> pastAppointmentsDates = new ArrayList<Date>();
                ArrayList<Appointment> pastAppointments = new ArrayList<Appointment>();
                for (DataSnapshot appointment:appointmentSnapshot.getChildren()){
                    Appointment apt = appointment.getValue(Appointment.class);
                    Date appointmentDate = apt.getStartTime();
                    pastAppointmentsDates.add(appointmentDate);
                }
                Collections.sort(pastAppointmentsDates);

                for (Date date:pastAppointmentsDates){
                    for (DataSnapshot appointment:appointmentSnapshot.getChildren()){
                        Appointment apt = appointment.getValue(Appointment.class);
                        if (date.equals(apt.getStartTime())
                                && patientID.equals(apt.getPatientID())
                                && !pastAppointments.contains(apt)){
                            pastAppointments.add(apt);
                        }
                    }
                }

                for (Appointment apt:pastAppointments){
                    getAppointment(apt.getStartTime(), apt.getDoctorID());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getAppointment(Date date, String doctorID){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("doctors");
        LinearLayout layout = (LinearLayout) findViewById(R.id.past_appointment_list);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot doctor:snapshot.getChildren()){
                    Doctor doc = doctor.getValue(Doctor.class);
                    if (doctorID.equals(doctor.getKey())){
                        Calendar currentTime = Calendar.getInstance();
                        Date currentDate = currentTime.getTime();
                        if (date.compareTo(currentDate) < 0){
                            appointmentInfo = new TextView(PatientViewPastAppointmentActivity.this);
                            SimpleDateFormat formattedDate = new SimpleDateFormat("EEE MMM d 'at' h:mm a");
                            String time = formattedDate.format(date);
                            appointmentInfo.setText(time + " with Dr." + doc.getName() + "\n");
                            Resources resource = (Resources) getBaseContext().getResources();
                            ColorStateList csl = (ColorStateList) resource.getColorStateList(R.color.black);
                            appointmentInfo.setTextColor(csl);
                            appointmentInfo.setGravity(Gravity.CENTER);
                            appointmentInfo.setTextSize(15);
                            layout.addView(appointmentInfo);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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