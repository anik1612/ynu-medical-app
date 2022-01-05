package com.example.ynumedical.appointment_activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.ynumedical.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class PatientBookAppointmentActivity extends AppCompatActivity {

    Button newSlot;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_book_appointment);

        auth = FirebaseAuth.getInstance();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Select Time Slots");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2E5DA3")));

        Intent intent = getIntent();
        String doctorID = intent.getStringExtra("doctorID");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        //Creating blank schedule
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                int i = 0;

                TimeZone.setDefault(TimeZone.getTimeZone("EST"));

                Calendar today = Calendar.getInstance();
                today.add(Calendar.DAY_OF_MONTH, 1);
                today.set(Calendar.HOUR, -15);
                today.set(Calendar.MINUTE, 0);
                today.set(Calendar.SECOND, 0);


                Calendar holder = new GregorianCalendar();
                holder = today;


                while(i<30){
                    int j = 0;
                    int k = holder.get(Calendar.HOUR_OF_DAY);
                    int m = 9;
                    if(i == 0 && k > 17){
                        holder.set(Calendar.HOUR_OF_DAY, 9);
                        holder.add(Calendar.DAY_OF_MONTH, 1);
                    }
                    else if (i == 0 && k < 9){
                        holder.set(Calendar.HOUR_OF_DAY, 9);
                    }
                    else if (i == 0 && k >= 9 && k <= 17){
                        int t = k - 9;
                        m = m - t;
                    }

                    while (j<9){
                        Calendar slot = new GregorianCalendar();
                        slot = holder;

                        Date date = slot.getTime();

                        Calendar now = Calendar.getInstance();


                        SimpleDateFormat test = new SimpleDateFormat("yyyyMMddhhmm");
                        String dateCode = test.format(date)+doctorID;



                        if(!snapshot.child("Appointments").child(dateCode).exists() && now.before(slot))
                            addTimeSlot(date, doctorID); //should be in if statement once all features are added

                        slot.add(Calendar.HOUR, 1);
                        holder = slot;
                        j++;
                    }
                    holder.add(Calendar.HOUR, -9);
                    holder.add(Calendar.DAY_OF_MONTH,1);
                    i++;
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public void addTimeSlot(Date date, String doctorID){
        LinearLayout layout = (LinearLayout) findViewById(R.id.time_slots);
        newSlot = new Button(this);

        Context pageContext = this;
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d 'at' h:mm a");
        dateFormat.setTimeZone(TimeZone.getTimeZone("EST"));
        String time = dateFormat.format(date);
        newSlot.setText(time);
        newSlot.setAllCaps(false);
        newSlot.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                bookAppointment(date, doctorID);
                new AlertDialog.Builder(pageContext)
                        .setTitle("Booking Successful")
                        .setMessage("You have successfully booked this timeslot!")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        });

        layout.addView(newSlot);

    }

    public void bookAppointment(Date date, String doctorID){


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Appointments");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String patientID = auth.getCurrentUser().getUid();

                Appointment apt = new Appointment(doctorID, patientID, date);

                String aptID = apt.getAppointmentID();

                ref.child(aptID).setValue(apt);

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