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

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText email , password ;
    private Button login ;
    private String userEmail , userPassword ;
    private UtilService utilService;
    private ProgressBar progressBar;
    private SharedPreference sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mapping();

        utilService = new UtilService();
        sharedPreference = new SharedPreference(getApplicationContext());

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                utilService.hide_keyboard(v,LoginActivity.this);
                userEmail = email.getText().toString().trim();
                userPassword = password.getText().toString().trim();

                if (validate(v))
                {
                    loginUser(v);
                }
            }
        });
    }

    private void loginUser(View view)
    {
        progressBar.setVisibility(View.VISIBLE);
        HashMap<String,String> params = new HashMap<>();
        params.put("email",userEmail);
        params.put("password",userPassword);

        String api = "https://todoapplicationsayan.herokuapp.com/api/todo/auth/login"; //Api Key

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, api, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getBoolean("success")){
                        String token = response.getString("token");
                     //   Toast.makeText(getApplicationContext(),token,Toast.LENGTH_LONG).show();
                        sharedPreference.setValue_string("token",token);
                        Toast.makeText(getApplicationContext(),"User LoggedIn",Toast.LENGTH_LONG).show();

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
                        Toast.makeText(LoginActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        requestQueue.add(jsonObjectRequest);
    }

    private boolean validate(View view)
    {
        boolean isValid = false ;

        if (!TextUtils.isEmpty(userEmail))
        {
            if (!TextUtils.isEmpty(userPassword))
            {
                isValid = true ;
            }
            else {
                utilService.showSnackBar(view,"Please Enter Password");
            }

        }
        else
        {
            utilService.showSnackBar(view,"Please Enter Email");
        }

        return  isValid;
    }

    private void mapping()
    {
        progressBar = findViewById(R.id.progress);
        email = findViewById(R.id.email);
        password =findViewById(R.id.password);
        login = findViewById(R.id.login);
    }

    public void createAccount(View view)
    {
        startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
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