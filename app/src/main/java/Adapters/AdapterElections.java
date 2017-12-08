package Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import AppClasses.Elections;
import AppConfig.LinkConfig;
import AppConfig.MySingleton;
import AppConfig.ParseJson;
import example.com.e_voting.CandidatesActivity;
import example.com.e_voting.R;
import example.com.e_voting.ResultList;

/**
 * Created by AAE on 2/23/2016.
 */
public class AdapterElections extends RecyclerView.Adapter<AdapterElections.ElectionViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Elections> electionsArrayList;
    private ParseJson parseJson;
    private ProgressDialog progressDialog;

    AlertDialog.Builder builder;
    AlertDialog alertDialog;

    public AdapterElections(Context context, ArrayList<Elections> electionsArrayList) {

        this.context = context;
        this.electionsArrayList = electionsArrayList;
        inflater = LayoutInflater.from(context);
        parseJson = new ParseJson();
        progressDialog = new ProgressDialog(context);

    }


    @Override
    public ElectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.row_election_list, parent, false);

        ElectionViewHolder viewHolder = new ElectionViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ElectionViewHolder holder, int position) {

        holder.txtName.setText(electionsArrayList.get(position).getNAME());
        holder.txtDate.setText(electionsArrayList.get(position).getDATE());
        holder.txtVenue.setText(electionsArrayList.get(position).getVENUE());


    }

    @Override
    public int getItemCount() {
        return electionsArrayList.size();
    }

    public class ElectionViewHolder extends RecyclerView.ViewHolder {


        TextView txtName, txtDate, txtVenue;
        Button btnStatus, btnResult, btnCandidates;


        public ElectionViewHolder(View itemView) {
            super(itemView);

            txtDate = (TextView) itemView.findViewById(R.id.txtElectionDate);
            txtName = (TextView) itemView.findViewById(R.id.txtElectionName);
            txtVenue = (TextView) itemView.findViewById(R.id.txtElectionVenue);

            btnCandidates = (Button) itemView.findViewById(R.id.btnCandidates);
            btnResult = (Button) itemView.findViewById(R.id.btnResult);
            btnStatus = (Button) itemView.findViewById(R.id.btnStatus);

            builder = new AlertDialog.Builder(context);


            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
alertDialog.dismiss();


                }
            });



            btnStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("######", "ID status :" + electionsArrayList.get(getAdapterPosition()).getID());

                    final int id = electionsArrayList.get(getAdapterPosition()).getID();
                    String name = electionsArrayList.get(getAdapterPosition()).getNAME();

                    builder.setTitle(name);
progressDialog.setMessage("please wait....");
                    progressDialog.show();
                    StringRequest request = new StringRequest(Request.Method.POST, LinkConfig.CHECK_ELECTION_STATUS, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
progressDialog.dismiss();
                            String status;
                            try {

                                 JSONObject object = new JSONObject(response);

                               status = object.getString("status");

                                 builder.setMessage("Status : "+status);
                                alertDialog= builder.create();
                                 alertDialog.show();


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
                            HashMap<String,String>params = new HashMap<String, String>();

                            params.put("electionId",String.valueOf(id));

                            return params;
                        }
                    };


                    MySingleton.getInstance(context).addToRequestQueue(request);



                }
            });

            btnResult.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("######", "ID result :" + electionsArrayList.get(getAdapterPosition()).getID());

                    int id = electionsArrayList.get(getAdapterPosition()).getID();
                    String name = electionsArrayList.get(getAdapterPosition()).getNAME();

                    Intent intent = new Intent(context, ResultList.class);

                    intent.putExtra("id", id);
                    intent.putExtra("name", name);

                    context.startActivity(intent);

                }
            });

            btnCandidates.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("######", "ID candidates :" + electionsArrayList.get(getAdapterPosition()).getID());

                    int id = electionsArrayList.get(getAdapterPosition()).getID();
                    String name = electionsArrayList.get(getAdapterPosition()).getNAME();

                    Intent intent = new Intent(context, CandidatesActivity.class);

                    intent.putExtra("id", id);
                    intent.putExtra("name", name);

                    context.startActivity(intent);



                }
            });


        }
    }
}
