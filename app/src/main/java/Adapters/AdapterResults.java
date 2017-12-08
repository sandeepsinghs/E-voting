package Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import AppClasses.Result;
import AppConfig.LinkConfig;
import example.com.e_voting.R;

/**
 * Created by AAE on 2/23/2016.
 */
public class AdapterResults extends RecyclerView.Adapter<AdapterResults.ResultViewHolder>{

private Context context;
    private LayoutInflater inflater;
    private ArrayList<Result>resultArrayList;

    public AdapterResults(Context context,ArrayList<Result>resultArrayList){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.resultArrayList = resultArrayList;

    }


    @Override
    public ResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.row_result_list,parent,false);

        ResultViewHolder viewHolder = new ResultViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ResultViewHolder holder, int position) {

        holder.txtVotes.setText(resultArrayList.get(position).getVOTES());
        holder.txtName.setText(resultArrayList.get(position).getNAME());

       String imgUrl = LinkConfig.PHOTO_URL+resultArrayList.get(position).getIMAGE();


        Picasso.with(context).load(imgUrl).error(R.drawable.dummy_profile).into(holder.imgUser);

    }

    @Override
    public int getItemCount() {
        return resultArrayList.size();
    }

    public class ResultViewHolder extends RecyclerView.ViewHolder{

        TextView txtName,txtVotes;
        CircularImageView imgUser;

        public ResultViewHolder(View itemView) {
            super(itemView);

            imgUser = (CircularImageView) itemView.findViewById(R.id.imgResultCandidatePic);
            txtName = (TextView) itemView.findViewById(R.id.txtResultCandidateName);
            txtVotes = (TextView) itemView.findViewById(R.id.txtResultVotes);
        }
    }
}
