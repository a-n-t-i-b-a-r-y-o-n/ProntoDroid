package paronomasia.audioir;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
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


    public enum Pressed {
        POWER, INPUT, VOLUP, VOLDN, CHANUP, CHANDN, UP, DOWN, LEFT, RIGHT, SELECT, MENU
    }

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
        pwrButton.setOnClickListener(l -> transmitCode(R.raw.sanyopower, Pressed.POWER));


        Button inputButton = findViewById(R.id.inputButton);
        inputButton.setOnClickListener(l -> transmitCode(R.raw.sanyoinput, Pressed.INPUT));

        Button volUPButton = findViewById(R.id.volUPButton);
        volUPButton.setOnClickListener(l -> transmitCode(R.raw.sanyovolup, Pressed.VOLUP));

        Button volDNButton = findViewById(R.id.volDNButton);
        volDNButton.setOnClickListener(l -> transmitCode(R.raw.sanyovoldown, Pressed.VOLDN));

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


    public void transmitCode(int wavFile, Pressed pressed){

        // Determine and set max volume
        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
        int origVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

        // Initiate playback
        MediaPlayer mp = MediaPlayer.create(this, wavFile);
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setVolume(1, 1);
        mp.start();

        // Reset volume to original setting for the user
        mp.setOnCompletionListener(l -> am.setStreamVolume(AudioManager.STREAM_MUSIC, origVol, 0));
    }
}
