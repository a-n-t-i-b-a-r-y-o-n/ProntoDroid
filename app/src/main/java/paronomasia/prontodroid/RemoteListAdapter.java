package paronomasia.prontodroid;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RadioButton;

import java.util.ArrayList;

/**
 * Braden 9/17/17.
 */

public class RemoteListAdapter extends RecyclerView.Adapter<RemoteListAdapter.ViewHolder> {

    private ArrayList<Remote> rList;
    private RemotesDBHelper rdb;

    public RemoteListAdapter(ArrayList<Remote> rList){
        this.rList = rList;
    }

    @Override
    public RemoteListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        // Create a view
        RadioButton r = (RadioButton) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.remotelist_radio_button, parent, false);
        // Set the attributes here:


        return new ViewHolder(r);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int i){
        // get element from dataset at this position, then
        // replace the contents of the view with that element.
        holder.button.setText(rList.get(i).getName());
        holder.button.setChecked(rList.get(i).getCurrent());

    }

    @Override
    public int getItemCount(){
        return rList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public RadioButton button;

        public ViewHolder(RadioButton r) {
            super(r);
            button = r;

            r.setOnClickListener(v -> {

                // Check only one button, and set all the others' "current" value to 0
                if(r.isChecked()){
                    rdb = new RemotesDBHelper(r.getContext());
                    rdb.clearCurrent();
                    for(int x = 0; x < rList.size(); x++)
                        rList.get(x).setCurrent(false);
                    rdb.updateCurrent(rList.get(getAdapterPosition()).getID());
                    rList.get(getAdapterPosition()).setCurrent(true);
                    notifyDataSetChanged();
                }

                notifyDataSetChanged();
            });
        }
    }
}
