package paronomasia.audioir;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;

public class Controls extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controls);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(l -> Snackbar.make(l, "Not implemented yet...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show());


        ImageButton pwrButton = findViewById(R.id.pwrButton);
        pwrButton.setOnClickListener(l -> {
            MediaPlayer pwrMP = MediaPlayer.create(this, R.raw.sanyopower);
            pwrMP.start();
            //mp.setVolume(100,100);
        });


        Button inputButton = findViewById(R.id.inputButton);
        inputButton.setOnClickListener(l -> {
            MediaPlayer inputMP = MediaPlayer.create(this, R.raw.sanyoinput);
            inputMP.start();
        });

        Button volUPButton = findViewById(R.id.volUPButton);
        volUPButton.setOnClickListener(l -> {
            MediaPlayer volupMP = MediaPlayer.create(this, R.raw.sanyovolup);
            volupMP.start();
        });

        Button volDNButton = findViewById(R.id.volDNButton);
        volDNButton.setOnClickListener(l -> {
            MediaPlayer voldnMP = MediaPlayer.create(this, R.raw.sanyovoldown);
            voldnMP.start();
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_controls, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
