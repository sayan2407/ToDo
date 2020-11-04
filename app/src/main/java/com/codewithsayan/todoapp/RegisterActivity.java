package com.codewithsayan.todoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.codewithsayan.todoapp.UtilService.SharedPreference;
import com.codewithsayan.todoapp.UtilService.UtilService;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private Button register ;
    private ProgressBar progressBar;
    private TextInputEditText name,email,password ;
    private String userName,userEmail,userPassword ;
    private UtilService utilService ;
    private SharedPreference sharedPreference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mapping(); // Method For Declare Id

        utilService = new UtilService();
        sharedPreference = new SharedPreference(getApplicationContext());

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                utilService.hide_keyboard(v,RegisterActivity.this); //Hide Key Board
                userName = name.getText().toString().trim();
                userEmail = email.getText().toString().trim();
                userPassword = password.getText().toString().trim();

                if (validate(v))
                {
                    registerUser(v);
                }
            }
        });


    }
    private  void registerUser(View view)
    {
      //  Toast.makeText(this, "Name:"+name+"\nEmail:"+email+"\nPassword:"+password, Toast.LENGTH_SHORT).show();

        progressBar.setVisibility(View.VISIBLE);
        HashMap<String,String> params = new HashMap<>();
        params.put("username",userName);
        params.put("email",userEmail);
        params.put("password",userPassword);
        
        String api = "https://todoapplicationsayan.herokuapp.com/api/todo/auth/register"; //Api Key

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, api, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getBoolean("success")){
                        String token = response.getString("token");
                        sharedPreference.setValue_string("token",token);
                        Toast.makeText(getApplicationContext(),token,Toast.LENGTH_LONG).show();

                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        finish();
                    }
                    progressBar.setVisibility(View.GONE);
                }
                catch (JSONException je){

                    je.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                NetworkResponse response = error.networkResponse;

                if (error instanceof ServerError && response!=null)
                {
                    try {
                        String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers,"utf-8"));
                        JSONObject obj = new JSONObject(res);
                       // utilService.showSnackBar(view,obj.getString("msg"));
                        Toast.makeText(RegisterActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }catch (JSONException | UnsupportedEncodingException je){
                        je.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> headers = new HashMap<>();
                headers.put("Content-Type","application/json");

                return params;

            }
        };

        //Set Retry Policy
        int socketTime = 3000 ;
        RetryPolicy policy = new DefaultRetryPolicy(socketTime,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);

        //Request Add
        RequestQueue requestQueue = Volley.newRequestQueue(RegisterActivity.this);
        requestQueue.add(jsonObjectRequest);

    }

    public boolean validate(View view)
    {
        boolean isValid =  false;

        if (!TextUtils.isEmpty(userName))
        {

            if (!TextUtils.isEmpty(userEmail))
            {
                if (!TextUtils.isEmpty(userPassword))
                {
                    if (userPassword.length() > 5 )
                    {
                        isValid = true ;
                    }
                    else
                    {
                        utilService.showSnackBar(view,"Password should be atlest 6 Charectes");
                    }

                }
                else {
                    utilService.showSnackBar(view,"Please Enter Password");
                }
            }
            else {
                utilService.showSnackBar(view,"Please Enter Email");
            }

        }
        else
        {
            utilService.showSnackBar(view,"Please Enter User Name");
        }

        return  isValid ;
    }

    public void mapping()
    {
        progressBar = findViewById(R.id.progress);
        register = findViewById(R.id.register);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

    }

    public void loginHere(View view)
    {
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences todo_pref = getSharedPreferences("user_todo", Activity.MODE_PRIVATE);
        if (todo_pref.contains("token"))
        {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }
    }
}