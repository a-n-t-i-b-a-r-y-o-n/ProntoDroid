package paronomasia.audioir;

import android.content.Intent;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;

public class Controls extends AppCompatActivity {


    public enum Pressed {
        POWER, INPUT, VOLUP, VOLDN, CHANUP, CHANDN, UP, DOWN, LEFT, RIGHT, SELECT, MENU
    }

    public boolean fabMenu = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hide the status bar (do this first!)
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controls);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setImageResource(R.drawable.remote_icon_48dp);
        FloatingActionButton minifab_add = findViewById(R.id.minifab_add);
        FloatingActionButton minifab_settings = findViewById(R.id.minifab_settings);
        FloatingActionButton minifab_list = findViewById(R.id.minifab_list);

        ImageButton pwrButton = findViewById(R.id.pwrButton);
        ImageButton inputButton = findViewById(R.id.inputButton);
        ImageButton volUPButton = findViewById(R.id.volUPButton);
        ImageButton volDNButton = findViewById(R.id.volDNButton);

        // This opens the little FAB menu when you click on the regular FAB
        fab.setOnClickListener(l -> {
            if(fabMenu){
                closeFabMenu();
                Snackbar.make(l, "Editing isn't implemented yet...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            else {
                openFabMenu();
            }
        });

        fab.setOnFocusChangeListener((l, b) -> {
            //l = listener, b = boolean hasFocus ?
            if(!b){
                closeFabMenu();
            }
        });

        minifab_settings.setOnClickListener(l -> {
            closeFabMenu();
            Intent i = new Intent(Controls.this, Settings.class);
            startActivity(i);
        });

        minifab_list.setOnClickListener(l -> {
            closeFabMenu();
            Intent i = new Intent(Controls.this, RemoteList.class);
            startActivity(i);
        });

        minifab_add.setOnClickListener(l -> {
            closeFabMenu();
            Snackbar.make(l, "Adding buttons isn't implemented yet...", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        });



        pwrButton.setOnClickListener(l -> transmitCode(R.raw.sanyopower, Pressed.POWER));

        inputButton.setOnClickListener(l -> transmitCode(R.raw.sanyoinput, Pressed.INPUT));

        volUPButton.setOnClickListener(l -> transmitCode(R.raw.sanyovolup, Pressed.VOLUP));

        volDNButton.setOnClickListener(l -> transmitCode(R.raw.sanyovoldown, Pressed.VOLDN));

    }

    @Override
    protected void onResume(){
        // Hide the status bar (do this first!)
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        super.onResume();
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

    private void openFabMenu(){
        fabMenu = true;
        FloatingActionButton fab = findViewById(R.id.fab);
        FloatingActionButton minifab_add = findViewById(R.id.minifab_add);
        FloatingActionButton minifab_settings = findViewById(R.id.minifab_settings);
        FloatingActionButton minifab_list = findViewById(R.id.minifab_list);

        // Add new margins to the layout params
        FrameLayout.LayoutParams layoutParams1 = (FrameLayout.LayoutParams) minifab_add.getLayoutParams();
        layoutParams1.rightMargin += (int) (minifab_add.getWidth() * 1.7);
        layoutParams1.bottomMargin += (int) (minifab_add.getHeight() * 0.25);
        minifab_add.setLayoutParams(layoutParams1);

        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) minifab_settings.getLayoutParams();
        layoutParams2.rightMargin += (int) (minifab_settings.getWidth() * 1.5);
        layoutParams2.bottomMargin += (int) (minifab_settings.getHeight() * 1.5);
        minifab_settings.setLayoutParams(layoutParams2);

        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) minifab_list.getLayoutParams();
        layoutParams3.rightMargin += (int) (minifab_list.getWidth() * 0.25);
        layoutParams3.bottomMargin += (int) (minifab_list.getHeight() * 1.7);
        minifab_list.setLayoutParams(layoutParams3);

        // Adjust their visibility
        minifab_add.setVisibility(View.VISIBLE);
        minifab_settings.setVisibility(View.VISIBLE);
        minifab_list.setVisibility(View.VISIBLE);


        // Start the animations
        minifab_add.startAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.show_minifab1));
        minifab_settings.startAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.show_minifab2));
        minifab_list.startAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.show_minifab3));

        // Change main FAB icon to edit button
        fab.setImageResource(R.drawable.ic_create_white_48dp);

    }

    private void closeFabMenu(){
        fabMenu = false;
        FloatingActionButton fab = findViewById(R.id.fab);
        FloatingActionButton minifab_add = findViewById(R.id.minifab_add);
        FloatingActionButton minifab_settings = findViewById(R.id.minifab_settings);
        FloatingActionButton minifab_list = findViewById(R.id.minifab_list);

        // Do the opposite
        FrameLayout.LayoutParams layoutParams1 = (FrameLayout.LayoutParams) minifab_add.getLayoutParams();
        layoutParams1.rightMargin -= (int) (minifab_add.getWidth() * 1.7);
        layoutParams1.bottomMargin -= (int) (minifab_add.getHeight() * 0.25);
        minifab_add.setLayoutParams(layoutParams1);

        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) minifab_settings.getLayoutParams();
        layoutParams2.rightMargin -= (int) (minifab_settings.getWidth() * 1.5);
        layoutParams2.bottomMargin -= (int) (minifab_settings.getHeight() * 1.5);
        minifab_settings.setLayoutParams(layoutParams2);

        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) minifab_list.getLayoutParams();
        layoutParams3.rightMargin -= (int) (minifab_list.getWidth() * 0.25);
        layoutParams3.bottomMargin -= (int) (minifab_list.getHeight() * 1.7);
        minifab_list.setLayoutParams(layoutParams3);

        // Animations
        minifab_add.startAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.hide_minifab1));
        minifab_settings.startAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.hide_minifab2));
        minifab_list.startAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.hide_minifab3));

        minifab_add.setVisibility(View.INVISIBLE);
        minifab_settings.setVisibility(View.INVISIBLE);
        minifab_list.setVisibility(View.INVISIBLE);

        // Set main FAB icon back to normal
        fab.setImageResource(R.drawable.remote_icon_48dp);
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
