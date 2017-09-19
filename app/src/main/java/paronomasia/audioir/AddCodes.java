package paronomasia.audioir;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AddCodes extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private RemotesDBHelper rdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hide the status bar (do this first!)
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_codes);


        rdb = new RemotesDBHelper(AddCodes.this);


        FloatingActionButton fab = findViewById(R.id.fab);
        EditText codehex = findViewById(R.id.addcode_pronto);
        EditText codename = findViewById(R.id.addcode_name);
        Spinner codetype = findViewById(R.id.addcode_typemenu);

        codetype.setOnItemSelectedListener(this);
        ArrayAdapter<Code.buttonType> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Code.buttonType.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        codetype.setAdapter(adapter);


        fab.setOnClickListener(v -> {
            if(!codehex.getText().toString().equals("") && !codename.getText().toString().equals("")) {
                rdb.addCode(rdb.getCurrentRemote().getID(), new Code(-1, rdb.getCurrentRemote().getID(), codehex.getText().toString(), codetype.getSelectedItemPosition(), codename.getText().toString()));
                finish();
            }
            else
                Toast.makeText(this, "Please make sure all fields are filled.", Toast.LENGTH_SHORT)
                        .show();
        });


    }

    @Override
    protected void onResume(){
        // Hide the status bar (do this first!)
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        super.onResume();

        TextView remoteNameLabel = findViewById(R.id.remoteNameLabel);
        remoteNameLabel.setText(String.format("%s: %s", R.string.remoteName, rdb.getCurrentRemote().getName()));

        FloatingActionButton fab = findViewById(R.id.fab);
        EditText codehex = findViewById(R.id.addcode_pronto);
        EditText codename = findViewById(R.id.addcode_name);
        Spinner codetype = findViewById(R.id.addcode_typemenu);


        fab.setOnClickListener(v -> {
            if(!codehex.getText().toString().equals("") && !codename.getText().toString().equals("")) {
                rdb.addCode(rdb.getCurrentRemote().getID(), new Code(-1, rdb.getCurrentRemote().getID(), codehex.getText().toString(), codetype.getSelectedItemPosition(), codename.getText().toString()));
                finish();
            }
            else
                Toast.makeText(this, "Please make sure all fields are filled.", Toast.LENGTH_SHORT)
                        .show();
        });

    }



    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        switch(adapterView.getId()){
            case R.id.addcode_typemenu:
                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.WHITE);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
