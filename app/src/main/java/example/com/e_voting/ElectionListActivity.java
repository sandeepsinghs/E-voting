package example.com.e_voting;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Adapters.AdapterElections;
import AppClasses.Elections;
import AppConfig.LinkConfig;
import AppConfig.MySingleton;
import AppConfig.ParseJson;

public class ElectionListActivity extends AppCompatActivity {


    private RecyclerView listElections;
    private AdapterElections adapterElections;
    ArrayList<Elections> electionsArrayList;
    private ProgressDialog progressDialog;
    private ParseJson parseJson = new ParseJson();
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_election_list);

        user_id = getSharedPreferences(getPackageName(), MODE_PRIVATE).getString(LinkConfig.USERID, "");
        Log.e("TAG", " USer ID as " + user_id);
        init();


    }

    private void init() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Election Names");

        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.setMessage("Collecting Elections Info.....");

        listElections = (RecyclerView) findViewById(R.id.listElections);
        listElections.setLayoutManager(new LinearLayoutManager(this));

        electionsArrayList = new ArrayList<>();


        adapterElections = new AdapterElections(this, electionsArrayList);

        listElections.setAdapter(adapterElections);

        getElections();


    }

    private void getElections() {
        electionsArrayList.clear();
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, LinkConfig.GET_INCOMMING_ELECTIONS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();

                Log.d("####", "election response : " + response);
                try {
                    JSONArray array = new JSONArray(response);

                    Log.d("####", "election array : " + array.toString());

                    for (int i = 0; i < array.length(); i++) {

                        JSONObject object = array.getJSONObject(i);

                        String name = object.getString("name");
                        String date = object.getString("date");
                        String venue = object.getString("venue");
                        int id = object.getInt("id");


                        //   date = getDateTime(Long.parseLong(date));

                        Elections elections = new Elections(id, name, venue, date);

                        electionsArrayList.add(elections);

                        adapterElections.notifyDataSetChanged();


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.d("####", "election error : " + error.toString());

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();

                params.put("VoterId", user_id);

                return params;
            }
        };


        request.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);


    }


    public String getDateTime(long timestamp) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");


        String str = sdf.format(new java.util.Date(timestamp));

        return str;
    }

}
