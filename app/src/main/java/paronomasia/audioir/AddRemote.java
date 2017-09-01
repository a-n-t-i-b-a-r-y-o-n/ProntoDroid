package paronomasia.audioir;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class AddRemote extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_remote);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(l -> {

            // !!! DB TEST!!!
            String[] s1 = {"000", "000", "666"};
            Remote r1 = new Remote(s1, 666, Remote.deviceType.TV, "Sanyo TV");
            RemotesDBHelper rdbhelp = new RemotesDBHelper(AddRemote.this);
            if(rdbhelp.addRemote(r1)) {
                Snackbar.make(l, "Wrote successfully.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            else {
                Snackbar.make(l, "Something went wrong...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button readDBButton = findViewById(R.id.readDBButton);
        readDBButton.setOnClickListener(l -> {
            RemotesDBHelper rdbh = new RemotesDBHelper(AddRemote.this);
            List<Remote> list = rdbh.getRemotes();
            TextView output = findViewById(R.id.outputField);
            output.setText("");
            for(int i = 0; i < list.size(); i++){
                output.setText(output.getText() + "\nID: " + list.get(i).getID() +
                        "\tNAME: " + list.get(i).getName());
            }
        });

        Button purgeDBButton = findViewById(R.id.purgeDBButton);
        purgeDBButton.setOnClickListener(l -> {
            RemotesDBHelper rdbh = new RemotesDBHelper(AddRemote.this);
            rdbh.purgeDB();
            TextView output = findViewById(R.id.outputField);
            output.setText("Purged.");
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
