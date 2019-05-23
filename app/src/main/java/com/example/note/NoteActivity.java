package com.example.note;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class NoteActivity extends AppCompatActivity {

    private EditText mEtTitle;
    private EditText mEtContent;
    private String mNoteFileName;
    private Note mLoadedNote;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        mEtTitle = findViewById(R.id.note_et_title);
        mEtContent =  findViewById(R.id.note_et_content);
        mNoteFileName = getIntent().getStringExtra("NOTE_FILE");
        if(mNoteFileName != null && !mNoteFileName.isEmpty()){
            mLoadedNote=Utilities.getNoteByName(this,mNoteFileName);
            if (mLoadedNote != null){
                mEtTitle.setText(mLoadedNote.getmTitle());
                mEtContent.setText((mLoadedNote.getmContent()));
            }
        }
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
            case R.id.action_note_delete:
                deleteNote();
                break;
        }
        return true;
    }
    private void saveNote(){
        Note note;
        String title = mEtTitle.getText().toString();
        String content =  mEtContent.getText().toString();
        if(title.isEmpty()){
            Toast.makeText(this, "Title is empty", Toast.LENGTH_LONG).show();
            return;
        }
        if (mLoadedNote == null) {
            note = new Note(System.currentTimeMillis(),title , content);
        }else {
            note = new Note(mLoadedNote.getDateTime(), title, content);
        }
        if (Utilities.saveNote(this, note)){
            Toast.makeText(this, getString(R.string.toast_saved_message), Toast.LENGTH_SHORT).show();
            finish();
        }else {
            Toast.makeText(this, getString(R.string.toast_not_saved_message), Toast.LENGTH_SHORT).show();
        }
        finish();
    }
    private void deleteNote() {
        if (mLoadedNote == null){
            finish();
        }else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                    .setTitle("Delete")
                    .setMessage("Are you really want to delete it, are you sure?")
                    .setPositiveButton("Yes", (DialogInterface dia, int which)-> {
                        Utilities.deleteNote(getApplicationContext(),mLoadedNote.getDateTime()+Utilities.FILE_EXTENTION);
                        Toast.makeText(getApplicationContext(),"Note is deleted!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setNegativeButton("No", null)
                    .setCancelable(false);
            dialog.show();
        }
    }
}