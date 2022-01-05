package com.example.ynumedical.user_information;

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
import com.example.ynumedical.appointment_activities.Appointment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class PatientViewVisitedDoctorsActivity extends AppCompatActivity {

    FirebaseAuth auth;
    TextView doctorInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_view_visited_doctors);

        auth = FirebaseAuth.getInstance();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("See Visited Doctors");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2E5DA3")));

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot appointmentSnapshot = snapshot.child("Appointments");
                ArrayList<String> doctorName = new ArrayList<String>();
                for (DataSnapshot appointment:appointmentSnapshot.getChildren()){
                    String patientID = auth.getUid();
                    Appointment apt = appointment.getValue(Appointment.class);
                    if (patientID.equals(apt.getPatientID())){
                        doctorName = getDoctor(apt.getStartTime(), apt.getDoctorID(), doctorName);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public ArrayList<String> getDoctor(Date date, String doctorID, ArrayList<String> doctorName){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("doctors");
        LinearLayout layout = (LinearLayout) findViewById(R.id.doctor_list);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot doctor:snapshot.getChildren()){
                    Doctor doc = doctor.getValue(Doctor.class);
                    if (doctorID.equals(doctor.getKey())){
                        Calendar currentDate = Calendar.getInstance();
                        Date currentTime = currentDate.getTime();
                        if (date.compareTo(currentTime) < 0){
                            if (!doctorName.contains(doc.getName())){
                                doctorName.add(doc.getName());
                                doctorInfo = new TextView(PatientViewVisitedDoctorsActivity.this);
                                doctorInfo.setText("Dr." + doc.getName() + ", specializes in " + doc.getSpecialty() + "\n");
                                Resources resource = (Resources) getBaseContext().getResources();
                                ColorStateList csl = (ColorStateList) resource.getColorStateList(R.color.black);
                                doctorInfo.setTextColor(csl);
                                doctorInfo.setGravity(Gravity.CENTER);
                                doctorInfo.setTextSize(15);
                                layout.addView(doctorInfo);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return doctorName;
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