package paronomasia.audioir;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableRow;

import java.util.ArrayList;

public class CodeList extends AppCompatActivity {

    /*
    TODO
        - Implement some type of scrolling list view (RecyclerView? ListView? Nested Scrolling View?)
        - Make this actually pull from the DB
     */

    Remote current;
    RemotesDBHelper rdb = new RemotesDBHelper(CodeList.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hide the status bar (do this first!)
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_list);


        TableRow addButton = findViewById(R.id.addCodeTR);
        addButton.setOnClickListener(v ->{

            Intent i = new Intent(CodeList.this, AddCodes.class);
            startActivity(i);

        });
        ImageButton plusButton = findViewById(R.id.addCodeButton);
        plusButton.setOnClickListener(v -> addButton.callOnClick());


    }

    @Override
    protected void onResume(){
        // Hide the status bar
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        super.onResume();



        // Get all the codes from our current remote
        if(!rdb.getAllRemotes().isEmpty()) {
            ArrayList<Code> codes = rdb.getCodesForRemote(rdb.getCurrentRemote().getID());
        }
        else
            Log.d("DB", "The databse is empty though...");

        // Create the table with all of the codes and their symbols

    }

}


