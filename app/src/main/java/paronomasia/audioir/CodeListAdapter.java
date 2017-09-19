package paronomasia.audioir;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;

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
        // Set the attributes here:

        Log.d("DB", "onCreateViewHolder()");

        return new ViewHolder(l);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int i){
        // get element from dataset at this position, then
        // replace the contents of the view with that element.
        holder.label.setText(cList.get(i).getName());
        holder.label.setTextColor(Color.WHITE);
        Log.d("DB", "onBindViewHolder()");
        /*

        // This doesn't work yet. Look up how to use @BindDrawable in the Code.java file instead! (potentially as part of the enum)

        if(cList.get(i).getType() != Code.buttonType.OTHER.ordinal()){
            @BindDrawable(R.drawable.power)
            Drawable power;
            holder.image.setImageDrawable(power);
        }
        else {

        }
        */

    }

    @Override
    public int getItemCount(){
        return cList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout layout;
        public @BindView(R.id.codelist_codename) TextView label;
        public @BindView(R.id.codelist_buttonimage) ImageView image;


        public ViewHolder(LinearLayout l) {
            super(l);
            layout = l;
            ButterKnife.bind(this, l);
            label = (TextView) l.getChildAt(0);
            /*
            l.setOnClickListener(v -> {

                //Current selection:
                // cList.get(getAdapterPosition())

            });
            */
            Log.d("DB", "ViewHolder(LinearLayout l)");

        }
    }
}
