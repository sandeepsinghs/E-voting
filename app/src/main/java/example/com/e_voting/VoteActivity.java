package example.com.e_voting;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import AppConfig.LinkConfig;
import AppConfig.MultipartRequest;
import AppConfig.MySingleton;

public class VoteActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 101;
    private ProgressDialog progressDialog;
    ImageView imgVerifyThumb;
    boolean isThumbSelected;
    private EditText edtPassword;
    private Button btnOK;
    String thumbPath;
    public static final int REQUEST_THUMB = 11;
    private int voter_id, election_id, candidate_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if (Build.VERSION.SDK_INT >= 23) {

            if (checkPermission()) {


            } else {
                requestPermission();
            }

        }


        progressDialog = new ProgressDialog(this);

        voter_id = getIntent().getIntExtra("v_id", 0);
        election_id = getIntent().getIntExtra("e_id", 0);
        candidate_id = getIntent().getIntExtra("c_id", 0);

        imgVerifyThumb = (ImageView) findViewById(R.id.imgVerifyThumb);
        btnOK = (Button) findViewById(R.id.btnSetVote);
        edtPassword = (EditText) findViewById(R.id.edtVotePassword);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String password = edtPassword.getText().toString();


                if (password.equals("")) {

                } else if (!isThumbSelected) {

                } else {

                    verifyThumb(thumbPath, password, voter_id);
                }


            }
        });


        imgVerifyThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                intent1.setType("image/*");

                startActivityForResult(Intent.createChooser(intent1, "Select Image"), REQUEST_THUMB);
            }
        });


    }

    private void verifyThumb(String thumbPath, String password, final int voter_id) {

        progressDialog.setMessage("Verifying User Authentication.... ");
        progressDialog.show();

        HashMap<String, String> params = new HashMap<>();
        params.put("voterId", String.valueOf(voter_id));
        params.put("password", password);


        Log.d("######", "params : " + params.toString());


        MultipartRequest multipartRequest = new MultipartRequest(LinkConfig.VOTER_THUMB_AUTH, thumbPath, params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();

                Log.d("######", "Auth Response " + response);

                try {
                    JSONObject object = new JSONObject(response);

                    int code = object.getInt("code");

                    switch (code) {
                        case 200:
                            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();

                            doVote(voter_id, election_id, candidate_id);

                            break;
                        case 201:
                            Toast.makeText(getApplicationContext(), "Password Not Matched", Toast.LENGTH_LONG).show();

                            edtPassword.setText("");
                            break;
                        case 202:
                            Toast.makeText(getApplicationContext(), "Thumb image Not Matched", Toast.LENGTH_LONG).show();
                            edtPassword.setText("");
                            imgVerifyThumb.setImageResource(R.mipmap.ic_launcher);

                            break;
                        case 203:
                            Toast.makeText(getApplicationContext(), "Already Voted", Toast.LENGTH_LONG).show();
                            finish();


                            break;


                    }


                } catch (JSONException e) {


                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.d("######", "Auth Errror " + error.toString());
            }
        });

        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(45 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(multipartRequest);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_THUMB) {

            if (data.getData() != null) {
                Toast.makeText(getApplicationContext(), "result code " + requestCode, Toast.LENGTH_SHORT).show();
                isThumbSelected = true;

                Uri selectedImage = data.getData();

                thumbPath = getRealPathFromURI(selectedImage);

                Log.d("######", "picture path " + thumbPath);

                Bitmap bmp2 = (BitmapFactory.decodeFile(thumbPath));

                imgVerifyThumb.setImageBitmap(bmp2);
            }

        }

    }

    private void doVote(final int voterId, final int electionId, final int candidateId) {


        progressDialog.setMessage("Voting in progress....");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, LinkConfig.CAST_VOTE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();

                try {
                    JSONObject object = new JSONObject(response);

                    Log.d("####", "Response :" + object.toString());
                    if (object.getString("result").equals("success")) {
                        Toast.makeText(getApplicationContext(), "Voted Successfully", Toast.LENGTH_SHORT).show();

                        finish();

                        Log.d("####", "voterId : " + voterId + " election id : " + electionId + " candidate id : " + candidateId);


                    } else {
                        Toast.makeText(getApplicationContext(), "Couldn't vote,please try again", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.d("####", "Error :" + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> params = new HashMap<>();

                params.put("VoterId", String.valueOf(voterId));
                params.put("ElectionId", String.valueOf(electionId));
                params.put("CandidateId", String.valueOf(candidateId));

                return params;
            }
        };

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);


    }

    private String getRealPathFromURI(Uri contentUri) {
        try {
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor c = this.getContentResolver().query(contentUri, filePath, null, null, null);

            int columnIndex = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            //c.getColumnIndex(filePath[0]);
            c.moveToFirst();
            return c.getString(columnIndex);
        } catch (Exception e) {
            return "";
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {

            return true;

        } else {

            return false;

        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

            Toast.makeText(this, "GPS permission allows us to access location data. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }


}
