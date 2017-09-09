package paronomasia.audioir;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AddRemote extends AppCompatActivity implements OnItemSelectedListener {

    /*
    TODO
        - Handle adding a new vendor
        - Handle tracking a new device type?

    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_remote);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RemotesDBHelper rdb = new RemotesDBHelper(AddRemote.this);

        FloatingActionButton fab = findViewById(R.id.fab);
        EditText remoteNameField = findViewById(R.id.remoteNameField);
        EditText codeField = findViewById(R.id.remoteCodeField);
        Spinner vendorMenu = findViewById(R.id.vendorMenu);
        Spinner typeMenu = findViewById(R.id.typeMenu);


        // ~~ ADD TEST VALUES ~~
        rdb.addVendor(1, "Sanyo");
        rdb.addVendor(2, "Sony");
        rdb.addVendor(666, "Paronomasia");


        fab.setOnClickListener(v -> {

            if (!remoteNameField.getText().toString().equals("")) {
                // !!! DB TEST!!!
                ArrayList<String> s1 = new ArrayList<>();
                s1.add(codeField.getText().toString());

                // Remote(ArrayList<String> codes, int vendor, deviceType type, String name, boolean current, String hash)
                Remote r1 = new Remote(s1, rdb.getVendor(vendorMenu.getSelectedItem().toString()),
                        (Remote.deviceType) typeMenu.getSelectedItem(), remoteNameField.getText().toString(), true, "");
                if(rdb.addRemote(r1)) {
                    Snackbar.make(v, "Wrote successfully.", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
                else {
                    Snackbar.make(v, "Something went wrong...", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
            } else {
                Toast.makeText(v.getContext(), "Please enter a name.", Toast.LENGTH_SHORT).show();
            }
        });



        vendorMenu.setOnItemSelectedListener(this);
        ArrayList<String> vendorList = rdb.getAllVendors();

        // Is there a way to do this without hardcoding?
        vendorList.add("Add vendor +");

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, vendorList);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vendorMenu.setAdapter(adapter1);


        typeMenu.setOnItemSelectedListener(this);

        ArrayAdapter<Remote.deviceType> adapter2 = new ArrayAdapter<Remote.deviceType>(this, android.R.layout.simple_spinner_item, Remote.deviceType.values());
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeMenu.setAdapter(adapter2);




        Button readDBButton = findViewById(R.id.readDBButton);
        readDBButton.setOnClickListener(v -> {
            //RemotesDBHelper rdbh = new RemotesDBHelper(AddRemote.this);
            ArrayList<Remote> list = rdb.getAllRemotes();
            TextView output = findViewById(R.id.outputField);

            if (!list.isEmpty()) {
                output.setText("");
                for(int i = 0; i < list.size(); i++){
                    output.setText(output.getText() + "\nID: " + list.get(i).getID() +
                            "\tNAME: " + list.get(i).getName() +
                            "\tVENDOR: " + rdb.getVendor(list.get(i).getVendorId()) +
                            "\tTYPE: " + list.get(i).getType().toString() +
                            "\nCODES: " + list.get(i).getCodes().toString());
                }
            } else
                output.setText("Empty.");
        });

        Button purgeDBButton = findViewById(R.id.purgeDBButton);
        purgeDBButton.setOnClickListener(v -> {
            //RemotesDBHelper rdbh = new RemotesDBHelper(AddRemote.this);
            rdb.purgeDB();
            TextView output = findViewById(R.id.outputField);
            output.setText("Purged.");
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        RemotesDBHelper rdb = new RemotesDBHelper(adapterView.getContext());
        switch(adapterView.getId()){
            case R.id.vendorMenu:
                if(i == (adapterView.getCount() - 1)) {
                    /* The "Add new vendor" is inserted at the end, and this means
                        that we've clicked it. So, now we should turn the spinner to
                        an EditText and add the value to the Vendors table on submit,
                        linking the vendor _id column to the remote table accordingly.
                    */
                    Toast.makeText(adapterView.getContext(), "Adding vendors not yet implemented", Toast.LENGTH_SHORT).show();
                    adapterView.setSelection(0);
                }
                else {
                    //Toast.makeText(adapterView.getContext(), rdb.getVendor(adapterView.getItemAtPosition(i).toString()) + "", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(adapterView.getContext(), adapterView.getItemAtPosition(i).toString(), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.typeMenu:
                // do the normal thing
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
