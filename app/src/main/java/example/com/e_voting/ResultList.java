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

import Adapters.AdapterResults;
import AppClasses.Result;
import AppConfig.LinkConfig;
import AppConfig.MySingleton;
import AppConfig.ParseJson;

public class ResultList extends AppCompatActivity {

    private RecyclerView listResults;
    private AdapterResults adapterResults;
    private ArrayList<Result>resultArrayList;
    private int id;
    private String name;

    private ParseJson parseJson = new ParseJson();
    private ProgressDialog progressDialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_list);



        init();


    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        resultArrayList = new ArrayList<>();


        id = getIntent().getIntExtra("id",0);
        name = getIntent().getStringExtra("name");

        getSupportActionBar().setTitle(name);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Getting Result for "+name+"....");




    listResults = (RecyclerView) findViewById(R.id.listResults);
        listResults.setLayoutManager(new LinearLayoutManager(this));

adapterResults = new AdapterResults(this,resultArrayList);

        listResults.setAdapter(adapterResults);

        getResult();

    }

    private void getResult() {
progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, LinkConfig.GET_RESULT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
progressDialog.dismiss();

                try {
                    JSONArray array = new JSONArray(response);

                    if (!array.isNull(0) && array.length()>0){

                      for (int i=0;i<array.length();i++){

                          JSONObject object = array.getJSONObject(i);

                          Log.d("######","List "+object.toString());

                         String candidateId = object.getString("candidateId");
                          String name = object.getString("name");
                          String votes = object.getString("votes");
                          String image = object.getString("image");

                       Result r = new Result(name,votes,candidateId,image);

                          resultArrayList.add(r);

                          adapterResults.notifyDataSetChanged();


                      }

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

                params.put("electionId",String.valueOf(id));

                return params;
            }
        };

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);

    }

}
