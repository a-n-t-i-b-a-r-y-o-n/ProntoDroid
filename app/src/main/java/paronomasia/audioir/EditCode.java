package paronomasia.audioir;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class EditCode extends AppCompatActivity {

    RemotesDBHelper rdb = new RemotesDBHelper(EditCode.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_code);

        // Get the id of the code we're editing from the intent.
        int id = 1;


        Code code = rdb.getCodeByID(id);

        TextView nameText = findViewById(R.id.codeNameText);
        nameText.setText(code.getName());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            rdb.updateCode(code);

        });
    }

}
