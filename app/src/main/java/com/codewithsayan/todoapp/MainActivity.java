package com.codewithsayan.todoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.codewithsayan.todoapp.UtilService.SharedPreference;

public class MainActivity extends AppCompatActivity {

    SharedPreference sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreference = new SharedPreference(getApplicationContext());
    }

    public void logout(View view)
    {
        sharedPreference.clear();
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        finish();
    }
}