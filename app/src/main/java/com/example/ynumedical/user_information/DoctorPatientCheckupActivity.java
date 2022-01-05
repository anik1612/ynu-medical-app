package com.example.ynumedical.user_information;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DoctorPatientCheckupActivity extends AppCompatActivity {

    FirebaseAuth auth;
    TextView patientInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_patient_checkup);

        auth = FirebaseAuth.getInstance();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("See Patient Info");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2E5DA3")));

        LinearLayout layout = (LinearLayout) findViewById(R.id.patient_list);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot patientsSnapshot = snapshot.child("patients");
                for(DataSnapshot patient:patientsSnapshot.getChildren()){
                    String doctorID = auth.getUid();
                    Patient pat = patient.getValue(Patient.class);

                    patientInfo = new TextView(DoctorPatientCheckupActivity.this);

                    String patientName = pat.getName();
                    String patUID = patient.getKey();
                    HealthInformation patientHI = pat.getHealthInformation();
                    String patientGender = patientHI.getGender();
                    String patientDOB = null;
                    if(patientHI.getDateOfBirth() != null){
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                        patientDOB = dateFormat.format(patientHI.getDateOfBirth());
                    }
                    int patientWeight = patientHI.getWeight();
                    patientInfo.setText(patientName+": ");
                    if (patientGender != null)
                        patientInfo.append("\n    Gender - "+patientHI.getGender());
                    if (patientDOB != null)
                        patientInfo.append("\n    Date of Birth - "+patientDOB);
                    if (patientWeight > 0)
                        patientInfo.append("\n    weight - "+patientHI.getWeight());

                    getPastDoctors(pat, patUID, patientInfo, layout);

//                    patientInfo.append("\n\n");

//                    layout.addView(patientInfo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void getPastDoctors(Patient pat, String patUID, TextView patientInfo, LinearLayout layout){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

//        ArrayList<Appointment> currPatientAppointments = pat.getAppointments();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String appointmentListText = "";

                DataSnapshot doctorSnpashot = snapshot.child("doctors");
                DataSnapshot appointmentSnapshot = snapshot.child("Appointments");
                for(DataSnapshot apt: appointmentSnapshot.getChildren()){
                    String curAptKey = apt.getKey();
                    String curPatientUID = apt.child("patientID").getValue(String.class);

                    Log.i("PATUID CHECK", patUID+curPatientUID);
                    if(patUID.equals(curPatientUID)) {
                        Log.i("PATUID MATCHES", "matching");
                        Appointment aptClass = apt.getValue(Appointment.class);
                        TextView appointmentInfo = new TextView(DoctorPatientCheckupActivity.this);

                        Date date = aptClass.getStartTime();
                        String doctorID = aptClass.getDoctorID();

                        DataSnapshot doctorClass = doctorSnpashot.child(doctorID);
                        Doctor doc = doctorClass.getValue(Doctor.class);


                        Calendar currentTime = Calendar.getInstance();
                        Date currentDate = currentTime.getTime();
                        if (date.compareTo(currentDate) < 0) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d 'at' h:mm a");
                            String time = dateFormat.format(date);
                            //String doctorName = getDoctorNameFromID(aptClass.getDoctorID());
                            appointmentListText += "\n      " + " Dr. " + doc.getName() + " at "+time;
                        }
                    }
                }
                if(!appointmentListText.equals("")) {
                    patientInfo.append("\n    " + pat.name + " has had appointments with: ");
                    patientInfo.append(appointmentListText);
                }
                else{
                    patientInfo.append("\n    " + pat.name + " has had no prior appointments here.");
                }

                patientInfo.append("\n\n");
                Resources resource = (Resources) getBaseContext().getResources();
                ColorStateList csl = (ColorStateList) resource.getColorStateList(R.color.black);
                patientInfo.setTextColor(csl);
                patientInfo.setTextSize(13);
                layout.addView(patientInfo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    public String getDoctorNameFromID(String docID){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("doctors");

        final String[] docName = {""};
        final boolean[] finishRun = {false};
//        ConfirmCallback calConfirm

        //.child(""+ docID)
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot doc: snapshot.getChildren()){
                    docName[0] = doc.child("name").getValue(String.class);
                }
                finishRun[0] = true;
//                cb.callback(docName[0]);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return docName[0];
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