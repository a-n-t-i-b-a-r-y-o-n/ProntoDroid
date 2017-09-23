package paronomasia.prontodroid;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;


public class EditCode extends AppCompatActivity {

    RemotesDBHelper rdb = new RemotesDBHelper(EditCode.this);
    Code code;

    TextView nameText;
    EditText hex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_code);

        // Get the id of the code we're editing from the intent.
        this.code = rdb.getCodeByID(getIntent().getIntExtra("id", -1));

        nameText = findViewById(R.id.codeNameText);
        hex = findViewById(R.id.editcode_hex);


        nameText.setText(code.getName());
        hex.setText(code.getHex());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            code.setHex(hex.getText().toString());
            rdb.updateCode(code);
            finish();
        });
    }

}
