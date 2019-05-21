package com.example.note;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class NoteActivity extends AppCompatActivity {

    private EditText mEtTitle;
    private EditText mEtContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        mEtTitle = (EditText) findViewById(R.id.note_et_title);
        mEtContent = (EditText) findViewById(R.id.note_et_content);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note_new, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_note_save:
                saveNote();
                break;
        }
        return true;
    }
    private void saveNote(){
        Note note = new Note(System.currentTimeMillis(), mEtTitle.getText().toString(),mEtContent.getText().toString());

        if (Utilities.saveNote(this, note)){
            Toast.makeText(this, "Note has been saved!", Toast.LENGTH_SHORT).show();
            finish();
        }else {
            Toast.makeText(this, "Cannot save this note, please make sure that you have enough storage on your device!", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}










