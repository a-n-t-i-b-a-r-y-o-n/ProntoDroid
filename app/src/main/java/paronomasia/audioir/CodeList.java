package paronomasia.audioir;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableRow;

import java.util.ArrayList;

public class CodeList extends AppCompatActivity {

    /*
        TODO:
            - This thing doesn't work. Determine why.
     */

    private RecyclerView recycler;
    private RecyclerView.Adapter rAdapter;
    private RecyclerView.LayoutManager rLayoutManager;
    private RemotesDBHelper rdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hide the status bar (do this first!)
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_list);

        FloatingActionButton fab = findViewById(R.id.fab);

        rdb = new RemotesDBHelper(CodeList.this);

        recycler = findViewById(R.id.CodeListRecycler);

        recycler.setHasFixedSize(true);

        rLayoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(rLayoutManager);

        fab.setOnClickListener(v -> {
            Intent i = new Intent(this, AddCodes.class);
            startActivity(i);
        });


    }

    @Override
    protected void onResume(){
        // Hide the status bar
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        super.onResume();

        ArrayList<Code> c = rdb.dumpAllCodes();
        if(c == null){
            Log.d("DUMP", "Null list returned");
        }
        else if(c.size() == 0){
            Log.d("DUMP", "Empty list returned");
        }
        else if(c.size() > 0) {
            for (int i = 0; i < c.size(); i++){
                Log.d("DUMP", "ID: " + c.get(i).getID() + "\nRemote: " +
                        c.get(i).getRemoteID() + "\nHex: " + c.get(i).getHex());
            }
        }

        // Get all the codes from our current remote
        if(!rdb.getAllRemotes().isEmpty() && !rdb.getCodesForRemote(rdb.getCurrentRemote().getID()).isEmpty()) {
            rAdapter = new CodeListAdapter(rdb.dumpAllCodes());
            recycler.setAdapter(rAdapter);
            Log.d("DB", "Should've made an adapter?");
        }
        else
            Log.d("DB", "The database is empty though...");


    }

}


