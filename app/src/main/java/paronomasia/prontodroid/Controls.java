package paronomasia.prontodroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class Controls extends AppCompatActivity {

    /*
    TODO
        - Make the buttons (re)arrangeable
        - Implement the settings MiniFAB
     */

    public boolean fabMenu = false;
    RemotesDBHelper rdb;
    Remote current;

    private FloatingActionButton fab;
    private FloatingActionButton minifab_add;
    private FloatingActionButton minifab_settings;
    private FloatingActionButton minifab_list;
    private int mainFab = R.drawable.ic_remote_logo_white_48dp;

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

        fab.setImageResource(mainFab);

        // These will be created dynamically soon enough...
        ImageButton pwrButton = findViewById(R.id.pwrButton);
        ImageButton inputButton = findViewById(R.id.inputButton);
        ImageButton volUPButton = findViewById(R.id.volUPButton);
        ImageButton volDNButton = findViewById(R.id.volDNButton);
        ImageButton muteButton = findViewById(R.id.muteButton);


        this.rdb = new RemotesDBHelper(Controls.this);

        this.current = rdb.getCurrentRemote();


        // This opens the little FAB menu when you click on the regular FAB
        fab.setOnClickListener(v -> {
            if(fabMenu){
                closeFabMenu();
                Toast.makeText(this, "Editing isn't finished yet...", Toast.LENGTH_SHORT)
                        .show();
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


        // still hardcoded buttons, so they at least look for their code type.
        pwrButton.setOnClickListener(v -> {
            for(Code c : current.getCodes()){
                if(c.getType() == 0){
                    Pronto p = new Pronto(c.getHex());
                    p.play(getApplicationContext());
                }
            }
        });

        inputButton.setOnClickListener(v -> {
            for(Code c : current.getCodes()){
                if(c.getType() == 1){
                    Pronto p = new Pronto(c.getHex());
                    p.play(getApplicationContext());
                }
            }
        });

        volUPButton.setOnClickListener(v -> {
            for(Code c : current.getCodes()){
                if(c.getType() == 2){
                    Pronto p = new Pronto(c.getHex());
                    p.play(getApplicationContext());
                }
            }
        });

        volDNButton.setOnClickListener(v -> {
            for(Code c : current.getCodes()){
                if(c.getType() == 3){
                    Pronto p = new Pronto(c.getHex());
                    p.play(getApplicationContext());
                }
            }
        });

        muteButton.setOnClickListener(v -> {
            for(Code c : current.getCodes()){
                if(c.getType() == 4){
                    Pronto p = new Pronto(c.getHex());
                    p.play(getApplicationContext());
                }
            }
        });

    }


    @Override
    protected void onResume(){
        // Hide the status bar (do this first!)
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        super.onResume();

        TextView currentText = findViewById(R.id.currentText);

        if(this.current != null && rdb.getCurrentRemote() != null){
            if(this.current.hashCode() != rdb.getCurrentRemote().hashCode()) {
                // Reload the remote, something's changed.
                Log.d("AUDIOIR", "Remote changed. Reloading...");
                this.current = rdb.getCurrentRemote();
            }
            else {
                Log.d("AUDIOIR", "Remote unchanged.");
            }
            currentText.setText(this.current.getName());
        }
        else if(this.current == null && rdb.getAllRemotes() != null) {
            this.current = rdb.getCurrentRemote();
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
                    Log.d("AUDIOIR", "Lifecycle: Returned to Controls");
                    Log.d("AUDIOIR", "Result: " + data.getExtras().toString());
                }
                else {
                    Log.d("AUDIOIR", "Returned with result " + result);
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
            fab.setImageResource(mainFab);
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

}
