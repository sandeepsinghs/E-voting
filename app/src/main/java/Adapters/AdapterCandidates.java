package Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import AppClasses.Candidate;
import AppConfig.LinkConfig;
import AppConfig.ParseJson;
import example.com.e_voting.R;
import example.com.e_voting.VoteActivity;

/**
 * Created by AAE on 2/23/2016.
 */
public class AdapterCandidates extends RecyclerView.Adapter<AdapterCandidates.CandidateViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Candidate>candidateArrayList;
    private boolean isVoted;
    private int electionId,voterId,candidateId;
    AlertDialog alertDialog = null;
    private final int TAG = 1;
    private ParseJson parseJson;
private ProgressDialog progressDialog;
    private Set<String>votedList;
    public AdapterCandidates(Context context,ArrayList<Candidate>candidateArrayList,int electionId) {
        this.context = context;
        this.candidateArrayList = candidateArrayList;
        this.electionId = electionId;
        inflater = LayoutInflater.from(context);

votedList = new HashSet<>();

 votedList = context.getSharedPreferences(context.getPackageName(),context.MODE_PRIVATE).getStringSet("voted",null);
        voterId = Integer.parseInt(context.getSharedPreferences(context.getPackageName(), context.MODE_PRIVATE).getString(LinkConfig.USERID, "0"));


    }


    @Override
    public CandidateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_candidates_list, parent, false);

        CandidateViewHolder viewHolder = new CandidateViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CandidateViewHolder holder, final int position) {

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Processing........please wait");

      holder.txtAddress.setText(candidateArrayList.get(position).getADDRESS());
        holder.txtPhone.setText(candidateArrayList.get(position).getPHONE());
        holder.txtName.setText(candidateArrayList.get(position).getNAME());

        String imgUrl = LinkConfig.PHOTO_URL+candidateArrayList.get(position).getPHOTO_STRING();
        String symUrl = LinkConfig.PHOTO_URL+candidateArrayList.get(position).getSYMBOL_STRING();

        Log.d("#####","image url "+imgUrl);

        Log.d("#####","symbol url "+symUrl);

        Picasso.with(context).load(imgUrl).error(R.drawable.dummy_profile).into(holder.imgProfile);


        Picasso.with(context).load(symUrl).error(R.drawable.dummy_symbol).into(holder.imgSymbol);


       /* if (isVoted){

            Animation anim = AnimationUtils.loadAnimation(context,R.anim.zoom_out);

           anim.setAnimationListener(new Animation.AnimationListener() {
               @Override
               public void onAnimationStart(Animation animation) {

               }

               @Override
               public void onAnimationEnd(Animation animation) {

                   holder.cardVote.setVisibility(View.GONE);

               }

               @Override
               public void onAnimationRepeat(Animation animation) {

               }
           });

            holder.cardVote.startAnimation(anim);


        }else {
            holder.cardVote.setVisibility(View.VISIBLE);
        }
*/


       votedList = new HashSet<>();
   Set<String>simple = new HashSet<>();
        simple.add("0");

        Set<String> set = context.getSharedPreferences(context.getPackageName(),context.MODE_PRIVATE).getStringSet("voted",simple);

        votedList.addAll(set);

    if (votedList!=null){

        if (votedList.contains(String.valueOf(electionId))){

            Animation anim = AnimationUtils.loadAnimation(context,R.anim.zoom_out);

            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {

                    holder.cardVote.setVisibility(View.GONE);

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            holder.cardVote.startAnimation(anim);
        }else {
            holder.cardVote.setVisibility(View.VISIBLE);
        }

    }


        final AlertDialog.Builder builder= new AlertDialog.Builder(context);

        builder.setTitle("Do you want to vote for "+candidateArrayList.get(position).getNAME()+" ? ");
        builder.setMessage("Click yes to continue");

        builder.setPositiveButton("Vote", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                Intent intent = new Intent(context,VoteActivity.class);


                intent.putExtra("v_id",voterId);
                intent.putExtra("e_id",electionId);
                intent.putExtra("c_id",candidateId);


                context.startActivity(intent);


               // doVote(voterId, electionId, candidateId);

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });




       holder.cardVote.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

           candidateId = candidateArrayList.get(position).getID();



               alertDialog = builder.create();
               alertDialog.show();




           }
       });


    }

 /*   private void doVote(final int voterId, final int electionId, final int candidateId) {

        parseJson = new ParseJson();
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, LinkConfig.CAST_VOTE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
progressDialog.dismiss();

                try {
                    JSONObject object = parseJson.getJSONFromString(response);

                 Log.d("####","Response :"+object.toString());
                    if (object.getString("Result").equals("Success")){
                        Toast.makeText(context,"Voted Successfully",Toast.LENGTH_SHORT).show();


                        votedList.add(String.valueOf(electionId));
                        context.getSharedPreferences(context.getPackageName(),context.MODE_PRIVATE).edit()
                                .putStringSet("voted",votedList)
                                .apply();



                        Log.d("####","voterId : "+voterId+" election id : "+electionId+" canadidate id : "+candidateId);

                        isVoted = true;
                        notifyDataSetChanged();

                    }else {
                        Toast.makeText(context,"Couldn't vote,please try again",Toast.LENGTH_SHORT).show();
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
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String,String>params = new HashMap<>();

                params.put("VoterId",String.valueOf(voterId));
                params.put("ElectionId",String.valueOf(electionId));
                params.put("CandidateId",String.valueOf(candidateId));

                return params;
            }
        };

        MySingleton.getInstance(context).addToRequestQueue(request);


    }*/

    @Override
    public int getItemCount() {
        return candidateArrayList.size();
    }

    public class CandidateViewHolder extends RecyclerView.ViewHolder {


        TextView txtName,txtPhone,txtAddress;
        CircularImageView imgSymbol,imgProfile;
        CardView cardVote;


        public CandidateViewHolder(final View itemView) {
            super(itemView);

            txtAddress = (TextView) itemView.findViewById(R.id.txtCandidateAddress);
            txtName = (TextView) itemView.findViewById(R.id.txtCandidateName);
            txtPhone = (TextView) itemView.findViewById(R.id.txtCandidatePhone);
            cardVote = (CardView) itemView.findViewById(R.id.cardVote);
            imgProfile = (CircularImageView) itemView.findViewById(R.id.imgCandidatePic);
            imgSymbol = (CircularImageView) itemView.findViewById(R.id.imgSymbol);

            cardVote.setTag(TAG);




        }
    }

}
