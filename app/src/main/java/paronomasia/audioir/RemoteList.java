package paronomasia.audioir;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;


public class RemoteList extends AppCompatActivity {

    private RecyclerView recycler;
    private RecyclerView.Adapter rAdapter;
    private RecyclerView.LayoutManager rLayoutManager;
    private RemotesDBHelper rdb;

    protected void onCreate(Bundle savedInstanceState) {
        // Hide the status bar (do this first!)
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_list);

        rdb = new RemotesDBHelper(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        recycler = findViewById(R.id.MainRecycler);

        recycler.setHasFixedSize(true);

        rLayoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(rLayoutManager);


        fab.setOnClickListener(v -> {
            Intent i = new Intent(this, AddRemote.class);
            startActivity(i);
        });

    }

    @Override
    protected void onResume(){
        // Hide the status bar
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        if(rdb.getAllRemotes() != null) {
            rAdapter = new RemoteListAdapter(rdb.getAllRemotes());
            recycler.setAdapter(rAdapter);
        }
        else {
            Intent i = new Intent(this, AddRemote.class);
            startActivity(i);
        }

        super.onResume();
    }

}
