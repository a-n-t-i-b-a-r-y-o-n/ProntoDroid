package paronomasia.audioir;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Controls extends AppCompatActivity {

    /*
    TODO
        - Make the buttons (re)arrangeable
        - Link between each button and its Code.buttonType (for drawables and codes)
        - Implement the settings MiniFAB
     */

    public boolean fabMenu = false;
    RemotesDBHelper rdb;
    Remote current;

    private FloatingActionButton fab;
    private FloatingActionButton minifab_add;
    private FloatingActionButton minifab_settings;
    private FloatingActionButton minifab_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hide the status bar (do this first!)
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controls);

        fab = findViewById(R.id.controls_fab);
        minifab_add = findViewById(R.id.minifab_add);
        minifab_settings = findViewById(R.id.minifab_settings);
        minifab_list = findViewById(R.id.minifab_list);

        fab.setImageResource(R.drawable.remote_icon_48dp);

        // These will be created dynamically soon enough...
        ImageButton pwrButton = findViewById(R.id.pwrButton);
        ImageButton inputButton = findViewById(R.id.inputButton);
        ImageButton volUPButton = findViewById(R.id.volUPButton);
        ImageButton volDNButton = findViewById(R.id.volDNButton);
        ImageButton muteButton = findViewById(R.id.muteButton);
        ImageButton testProntoButton = findViewById(R.id.testProntoButton);


        this.rdb = new RemotesDBHelper(Controls.this);



        // test out the ProntoHEX generation/playing with the default Sanyo Power
        testProntoButton.setOnClickListener(v -> {
            Pronto p1 = new Pronto("0000 006C 0022 0002 015B 00AD 0016 0016 0016 0016 " +
                    "0016 0016 0016 0041 0016 0041 0016 0041 0016 0016 0016 0016 0016 0041 0016 " +
                    "0041 0016 0041 0016 0016 0016 0016 0016 0016 0016 0041 0016 0041 0016 0016 " +
                    "0016 0041 0016 0016 0016 0016 0016 0041 0016 0016 0016 0016 0016 0016 0016 " +
                    "0041 0016 0016 0016 0041 0016 0041 0016 0016 0016 0041 0016 0041 0016 0041 " +
                    "0016 05F7 015B 0057 0016 0E6C");
            p1.generateAndPlay();
            p1.debugPrintHex();
        });


        // This opens the little FAB menu when you click on the regular FAB
        fab.setOnClickListener(v -> {
            if(fabMenu){
                closeFabMenu();
                Snackbar.make(v, "Editing isn't implemented yet...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            else {
                openFabMenu();
            }
        });

        minifab_settings.setOnClickListener(v -> {
            closeFabMenu();

            // ~ THIS BUTTON PURGES THE DB AS OF RIGHT NOW ~
            rdb.purgeDB();

            Intent i = new Intent(Controls.this, Settings.class);
            startActivity(i);
        });

        minifab_list.setOnClickListener(v -> {
            closeFabMenu();
            Intent i = new Intent(this, RemoteList.class);
            startActivity(i);

        });

        minifab_add.setOnClickListener(v -> {
            closeFabMenu();
            if(this.current == null){
                Toast.makeText(this, "Please add a remote first.", Toast.LENGTH_SHORT)
                        .show();
            }
            else {
                Intent i = new Intent(Controls.this, CodeList.class);
                startActivity(i);
            }
        });


        pwrButton.setOnClickListener(v -> transmitCode(R.raw.sanyopower, Code.buttonType.POWER));

        inputButton.setOnClickListener(v -> transmitCode(R.raw.sanyoinput, Code.buttonType.INPUT));

        volUPButton.setOnClickListener(v -> transmitCode(R.raw.sanyovolup, Code.buttonType.VOLUP));

        volDNButton.setOnClickListener(v -> transmitCode(R.raw.sanyovoldown, Code.buttonType.VOLDN));

        muteButton.setOnClickListener(v -> Toast.makeText(v.getContext(), "Mute", Toast.LENGTH_SHORT).show());

    }


    @Override
    protected void onResume(){
        // Hide the status bar (do this first!)
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        super.onResume();

        TextView currentText = findViewById(R.id.currentText);

        this.current = rdb.getCurrentRemote();
        if(this.current != null){
            currentText.setText(this.current.getName());
        }
        else {
            currentText.setText(R.string.nullRemote);
        }

    }

    @Override
    protected void onActivityResult(int request, int result, Intent data){
        switch(request){
            case 1:
                if(result == RESULT_OK){
                    // Determine the remote selected and change the UI accordingly.
                    int res = data.getIntExtra("remote", 0);
                    Log.d(" ~ Lifecycle ~ ", "Returned to Controls");
                    Log.d("Result", data.getExtras().toString());
                }
                else {
                    Log.d(" ~ Lifecycle ~ ", "Returned with result " + result);
                }
                break;

        }


    }

    // #### FAB Menu methods

    private void openFabMenu(){
        if (!fabMenu) {
            fabMenu = true;

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

            // Grey out the codes button if we haven't added anything yet.
            if(this.current == null)
                minifab_add.setAlpha((float) 0.5);
            else
                minifab_add.setAlpha((float) 1.0);
        }

    }

    private void closeFabMenu(){

        if (fabMenu) {
            fabMenu = false;

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
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {

        // This method catches touch events, though here its primary usage is detecting touches
        // *outside* the little FAB menu in order to close it.
        // NOTE that it does so by using the locations of the one on top and the one furthest to the left.
        // ...this is open source, so if you have a better idea then by all means implement it...

        int[] fabLoc = new int[2];  // main fab (i.e. one furthest to the right/bottom)
        fab.getLocationOnScreen(fabLoc);
        int[] fabLLoc = new int[2]; // minifab furthest to left
        minifab_add.getLocationOnScreen(fabLLoc);
        int[] fabTLoc = new int[2]; // minifab furthest up
        minifab_list.getLocationOnScreen(fabTLoc);
        float x = e.getRawX();
        float y = e.getRawY();
        if ((x < fabLLoc[0] || x > fabLoc[0] + fab.getWidth()) || (y < fabTLoc[1] || y > fabLoc[1] + fab.getHeight())) {
            // Somewhere outside the FAB menu got clicked
            if(fabMenu){
                closeFabMenu();
                return super.dispatchTouchEvent(e);
            }
        }
        return super.dispatchTouchEvent(e);
    }


    public void transmitCode(int wavFile, Code.buttonType type){

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
