package paronomasia.audioir;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableRow;

public class RemoteList extends AppCompatActivity {

    /*
    TODO
        - Implement some type of scrolling list view (RecyclerView? ListView? Nested Scrolling View?)
        - Make this actually pull from the DB
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hide the status bar (do this first!)
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_list);


        TableRow addButton = findViewById(R.id.addRemoteTR);
        addButton.setOnClickListener(v ->{

            Intent i = new Intent(RemoteList.this, AddRemote.class);
            startActivity(i);

        });
        ImageButton plusButton = findViewById(R.id.addRemoteButton);
        plusButton.setOnClickListener(v -> addButton.callOnClick());


    }

    @Override
    protected void onResume(){
        // Hide the status bar
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        super.onResume();
    }

    @Override
    protected void onStop(){
        Bundle res = new Bundle();
        res.putInt("remote", 1);
        Intent i = new Intent();
        i.putExtras(res);
        if (getParent() == null) {
            setResult(RemoteList.RESULT_OK, i);
        } else {
            getParent().setResult(RemoteList.RESULT_OK, i);
        }
        finish();
        super.onStop();
    }

}


