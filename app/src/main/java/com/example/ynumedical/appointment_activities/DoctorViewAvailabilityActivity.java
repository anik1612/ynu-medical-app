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

public class DoctorViewAvailabilityActivity extends AppCompatActivity {

    TextView timeSlot;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_view_availability);

        auth = FirebaseAuth.getInstance();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("See Available Time");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2E5DA3")));


        String doctorID = auth.getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();


        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //------Displaying Available time slots------//
                int i = 0;

                TimeZone.setDefault(TimeZone.getTimeZone("EST"));

                Calendar today = Calendar.getInstance();
                today.set(Calendar.MINUTE, 0);
                today.set(Calendar.SECOND, 0);


                Calendar holder = new GregorianCalendar();
                holder = today;

                addAvailableTextSlot();
                addEmptySlot();
                while(i<7){ //this week
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
                    while (j<m){
                        Calendar slot = new GregorianCalendar();
                        slot = holder;

                        Date date = slot.getTime();

                        Calendar now = Calendar.getInstance();

                        SimpleDateFormat test = new SimpleDateFormat("yyyyMMddhhmm");
                        String dateCode = test.format(date)+doctorID;



                        if(!snapshot.child("Appointments").child(dateCode).exists() && now.before(slot))
                            addTimeSlot(date);


                        slot.add(Calendar.HOUR, 1);
                        holder = slot;
                        j++;
                    }
                    holder.add(Calendar.HOUR, -9);
                    holder.add(Calendar.DAY_OF_MONTH,1);
                    i++;
                }

                //------Displaying Available time slots------//



                //------Displaying Appointment time slots------//
                TimeZone.setDefault(TimeZone.getTimeZone("EST"));

                today = Calendar.getInstance();
                today.set(Calendar.MINUTE, 0);
                today.set(Calendar.SECOND, 0);
                holder = new GregorianCalendar();
                holder = today;

                addLineSlot();
                addEmptySlot();
                addAppointmentTextSlot();
                addEmptySlot();

                i = 0;

                while(i<7){ //this week
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
                    while (j<m){
                        Calendar slot = new GregorianCalendar();
                        slot = holder;

                        Date date = slot.getTime();

                        Calendar now = Calendar.getInstance();

                        SimpleDateFormat test = new SimpleDateFormat("yyyyMMddhhmm");
                        String dateCode = test.format(date)+doctorID;



                        if(snapshot.child("Appointments").child(dateCode).exists() && now.before(slot))
                            getAppointment(date, dateCode);



                        slot.add(Calendar.HOUR, 1);
                        holder = slot;
                        j++;
                    }
                    holder.add(Calendar.HOUR, -9);
                    holder.add(Calendar.DAY_OF_MONTH,1);
                    i++;
                }
            }
            //------Displaying Appointment time slots------//

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    public void addTimeSlot(Date date){
        LinearLayout layout = (LinearLayout) findViewById(R.id.available_time_slots);
        timeSlot = new TextView(this);

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d 'at' h:mm a");
        String time = dateFormat.format(date);
        timeSlot.setText(time + " (available) " + "\n");
        Resources resource = (Resources) getBaseContext().getResources();
        ColorStateList csl = (ColorStateList) resource.getColorStateList(R.color.black);
        timeSlot.setTextColor(csl);
        timeSlot.setTextSize(15);
        timeSlot.setGravity(Gravity.CENTER);
        layout.addView(timeSlot);

    }


    public void getAppointment(Date date, String dateCode){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Appointments");
        LinearLayout layout = (LinearLayout) findViewById(R.id.available_time_slots);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot apt: snapshot.getChildren()){
                    Appointment aptm = apt.getValue(Appointment.class);


                    if(dateCode.equals(apt.getKey())){ //not sure if this is correct
                        timeSlot = new TextView(DoctorViewAvailabilityActivity.this);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d 'at' h:mm a");
                        String time = dateFormat.format(date);
                        timeSlot.setText("An appointment is booked at " + time + "\n");
                        Resources resource = (Resources) getBaseContext().getResources();
                        ColorStateList csl = (ColorStateList) resource.getColorStateList(R.color.black);
                        timeSlot.setTextColor(csl);
                        timeSlot.setTextSize(15);
                        timeSlot.setGravity(Gravity.CENTER);
                        layout.addView(timeSlot);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });






    }

    public void addEmptySlot(){
        LinearLayout layout = (LinearLayout) findViewById(R.id.available_time_slots);
        timeSlot = new TextView(this);

        timeSlot.setText("");

        layout.addView(timeSlot);

    }

    public void addLineSlot(){
        LinearLayout layout = (LinearLayout) findViewById(R.id.available_time_slots);
        timeSlot = new TextView(this);

        timeSlot.setText("--------------------------------------------------------------------------");

        layout.addView(timeSlot);

    }

    public void addAppointmentTextSlot(){
        LinearLayout layout = (LinearLayout) findViewById(R.id.available_time_slots);
        timeSlot = new TextView(this);

        timeSlot.setText("Your Upcoming Appointments:");

        layout.addView(timeSlot);

    }

    public void addAvailableTextSlot(){
        LinearLayout layout = (LinearLayout) findViewById(R.id.available_time_slots);
        timeSlot = new TextView(this);

        timeSlot.setText("Your Available Time Slots:");

        layout.addView(timeSlot);

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
