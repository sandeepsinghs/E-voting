package example.com.e_voting;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import AppConfig.GCMClientManager;
import AppConfig.LinkConfig;
import AppConfig.MySingleton;
import AppConfig.ParseJson;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    EditText edtLoginName, edtPassword;
    Button btnLogin, btnRegister;
    private TextView txtForgetPassword;
    private TextInputLayout layoutUsername, layoutPassword;
    private String loginName, password;

    private ParseJson parseJson = new ParseJson();

    private ProgressDialog progressDialog;
    private String PROJECT_NUMBER;

    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

    }

    private void init() {

        edtLoginName = (EditText) findViewById(R.id.edtLoginName);
        edtPassword = (EditText) findViewById(R.id.edtLoginPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        txtForgetPassword = (TextView) findViewById(R.id.txtForgetPassword);


        layoutPassword = (TextInputLayout) findViewById(R.id.layoutPassword);
        layoutUsername = (TextInputLayout) findViewById(R.id.layoutUserName);

        PROJECT_NUMBER = getResources().getString(R.string.projectNumber);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging In.....");

        loginName = getIntent().getStringExtra("email");

        edtLoginName.setText(loginName);

        builder = new AlertDialog.Builder(this);
        builder.setTitle("Password Sent");
        builder.setMessage("Your password has been sent to your registered email address,please check the email and continue login");

        builder.setNeutralButton("Ok !", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();

            }
        });


        btnRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        txtForgetPassword.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnLogin:

                loginName = edtLoginName.getText().toString();
                password = edtPassword.getText().toString();


                if (loginName.equals("") || !LinkConfig.EMAIL_ADDRESS_PATTERN.matcher(loginName).matches()) {
                    layoutUsername.setError("Please enter valid email id");

                    Toast.makeText(getApplicationContext(), "Please enter valid email id", Toast.LENGTH_SHORT).show();
                } else if (password.equals("")) {

                    layoutPassword.setError("Please enter valid password");
                    Toast.makeText(getApplicationContext(), "Please enter valid password", Toast.LENGTH_SHORT).show();

                } else {

                    layoutUsername.setError("");
                    layoutPassword.setError("");

                    doLogin();
                }


                break;


            case R.id.btnRegister:


                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));


                break;


            case R.id.txtForgetPassword:

                sendEmailToResetPasssword(loginName);

                break;


        }
    }

    private void sendEmailToResetPasssword(final String loginName) {
        progressDialog.setMessage("Sending Password To your email");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, LinkConfig.FORGET_PASSWORD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();

                try {
                    JSONObject object = new JSONObject(response);

                    if (object.getString("result").equals("success")) {

                        alertDialog = builder.create();
                        alertDialog.setCancelable(false);
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.show();

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> params = new HashMap<>();

                params.put("email", loginName);

                return params;
            }
        };

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);


    }

    private void doLogin() {
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, LinkConfig.LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();

                Log.d("####", "Response login : " + response);
                try {
                    JSONObject object = new JSONObject(response);

                    Log.d("####", "Response login : " + object.toString());

                    if (object.getString("result").equals("success")) {

                        String userId = object.getString("id");
                        getGCMId(userId);

                        Toast.makeText(getApplicationContext(), "Login Succeess", Toast.LENGTH_SHORT).show();

                        getSharedPreferences(getPackageName(), MODE_PRIVATE).edit().putString(LinkConfig.USERID, userId).apply();

                    } else {
                        Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                    Log.d("####", "Response login Exception : " + response);
                    Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();


                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.d("####", "Error login  : " + error.toString());

            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> params = new HashMap<>();

                params.put("emailId", loginName);
                params.put("password", password);


                return params;
            }
        };

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);


    }

    private void getGCMId(final String userId) {

        GCMClientManager gcmClientManager = new GCMClientManager(this, PROJECT_NUMBER);

        progressDialog.setMessage("Registering GCM...");
        progressDialog.show();

        gcmClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {
            @Override
            public void onSuccess(String registrationId, boolean isNewRegistration) {

                Log.d("####", "Registration id :" + registrationId);
                Log.d("####", "Registration status :" + isNewRegistration);

                progressDialog.dismiss();

//                if (isNewRegistration) {

                    updateGCMId(userId, registrationId);

                /*} else {

                    Toast.makeText(getApplicationContext(), "GCM is Registered already", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(LoginActivity.this, ElectionListActivity.class));

                    finish();


                }*/


            }

            @Override
            public void onFailure(String ex) {
                super.onFailure(ex);
                progressDialog.dismiss();


            }
        });


    }

    private void updateGCMId(final String userId, final String registrationId) {

        progressDialog.setMessage("Updating GCM ID....");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, LinkConfig.UPDATE_GCM, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.dismiss();

                try {
                    JSONObject object = new JSONObject(response);


                    Log.d("#####", "GCM Update response : " + object.toString());

                    startActivity(new Intent(LoginActivity.this, ElectionListActivity.class));

                    finish();


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();

            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> params = new HashMap<>();

                params.put("VoterId", userId);
                params.put("GCMId", registrationId);


                return params;
            }
        };

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);


    }
}
