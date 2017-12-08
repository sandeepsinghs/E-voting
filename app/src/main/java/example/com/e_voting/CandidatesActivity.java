package example.com.e_voting;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Adapters.AdapterCandidates;
import AppClasses.Candidate;
import AppConfig.LinkConfig;
import AppConfig.MySingleton;
import AppConfig.ParseJson;

public class CandidatesActivity extends AppCompatActivity {


    private RecyclerView listCandidates;
    private ProgressDialog progressDialog;
    private ParseJson parseJson = new ParseJson();
    private AdapterCandidates adapterCandidates;
private int ID;
    private String NAME;
    private ArrayList<Candidate> candidateArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidates);


        init();


    }

    private void init() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ID = getIntent().getIntExtra("id",0);
        NAME = getIntent().getStringExtra("name");

        Log.d("#####","Election id "+ID);


        getSupportActionBar().setTitle(NAME);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Getting Candidates for "+NAME +" .....");

        candidateArrayList = new ArrayList<>();


        listCandidates = (RecyclerView) findViewById(R.id.listCandidates);

        listCandidates.setLayoutManager(new LinearLayoutManager(this));


        adapterCandidates = new AdapterCandidates(this,candidateArrayList,ID);

        listCandidates.setAdapter(adapterCandidates);


        getCandidates();


    }

    private void getCandidates() {

      progressDialog.show();


        StringRequest request = new StringRequest(Request.Method.POST, LinkConfig.GET_CANDIDATES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
progressDialog.dismiss();

                try {
                    JSONArray array = new JSONArray(response);

                    for (int i=0;i<array.length();i++){

                        JSONObject object = array.getJSONObject(i);

                        String name = object.getString("name");
                        String address = object.getString("address");
                        String phone = object.getString("phone");
                        String photo = object.getString("photo");
                        String symbol = object.getString("symbol");

                        int id = object.getInt("id");

                        String gender = object.getString("gender");


                 Candidate c = new Candidate(name,address,phone,photo,symbol,id,gender);

                        candidateArrayList.add(c);

                        adapterCandidates.notifyDataSetChanged();


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
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String,String>params = new HashMap<>();

                params.put("electionId",String.valueOf(ID));


                return params;
            }
        };

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

}
