package paronomasia.audioir;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Braden 9/17/17.
 */

public class CodeListAdapter extends RecyclerView.Adapter<CodeListAdapter.ViewHolder> {

    private ArrayList<Code> cList;
    private RemotesDBHelper rdb;

    public CodeListAdapter(ArrayList<Code> cList){
        this.cList = cList;
    }

    @Override
    public CodeListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        // Create a view
        LinearLayout l = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.codelist_row, parent, false);
        // Set any layout attributes here.

        return new ViewHolder(l);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int i){
        // get element from dataset at this position, then
        // replace the contents of the view with that element.
        holder.label.setText(cList.get(i).getName());
        holder.label.setTextColor(Color.WHITE);

        // if there's a drawable associated with the given button
        // (*should* be blank if there's not ) then set it in the imageview
        holder.image.setImageDrawable(ContextCompat.getDrawable(holder.context, cList.get(i).getDrawableID()));

    }

    @Override
    public int getItemCount(){
        return cList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public Context context;
        public LinearLayout layout;
        public TextView label;
        public ImageView image;



        public ViewHolder(LinearLayout l) {
            super(l);
            context = l.getContext();
            layout = l;
            label = (TextView) l.getChildAt(0);
            image = (ImageView) l.getChildAt(1);


            l.setOnClickListener(v -> {

                //Current selection:
                Intent i = new Intent(l.getContext(), EditCode.class);
                i.putExtra("id", cList.get(getAdapterPosition()).getID());
                ContextCompat.startActivity(context, i, null);

            });

        }
    }
}
