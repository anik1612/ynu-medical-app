package com.example.ynumedical;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setActivityBackgroundColor(0xff4293f5, this);
        getSupportActionBar().setTitle("YNU MEDICAL");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2E5DA3")));

    }

    public static void setActivityBackgroundColor(int color, AppCompatActivity activity) {
        View view = activity.getWindow().getDecorView();
        view.setBackgroundColor(color);
    }

    /** Called when the user taps the login button */
    public void goToLoginPage(View view) {
        Intent intent = new Intent(this, LoginPage.class);
        startActivity(intent);
    }
}