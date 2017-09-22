package paronomasia.audioir;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

public class CodeList extends AppCompatActivity {

    private RecyclerView recycler;
    private RecyclerView.Adapter rAdapter;
    private RecyclerView.LayoutManager rLayoutManager;
    private RemotesDBHelper rdb;

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hide the status bar (do this first!)
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_list);

         fab = findViewById(R.id.codelist_fab);

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

        // Get all the codes from our current remote if that's a thing.
        if((rdb.getAllRemotes() == null || rdb.getCurrentRemote() == null || rdb.getCodesForRemote(rdb.getCurrentRemote().getID()) == null) || (rdb.getAllRemotes().isEmpty() || rdb.getCodesForRemote(rdb.getCurrentRemote().getID()).isEmpty())) {
            Log.d("DB", "This remote has no codes. Jumping to AddCodes.class...");
            Intent i = new Intent(this, AddCodes.class);
            startActivity(i);
        }
        else {
            rAdapter = new CodeListAdapter(rdb.getCodesForRemote(rdb.getCurrentRemote().getID()));
            recycler.setAdapter(rAdapter);
        }

    }

}


