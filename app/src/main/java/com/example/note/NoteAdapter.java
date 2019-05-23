package com.example.note;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class NoteAdapter extends ArrayAdapter<Note> {
    public NoteAdapter( Context context, int resource, ArrayList<Note> notes) {
        super(context, resource, notes);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_note, null);
        }
        Note note =getItem(position);
        if (note != null){
            TextView title = (TextView) convertView.findViewById(R.id.list_note_title);
            TextView date = (TextView) convertView.findViewById(R.id.list_note_daate);
            TextView content = (TextView) convertView.findViewById(R.id.list_note_content);
            String titleRawText = note.getmTitle();
            if(titleRawText.length() > 20){
                titleRawText = titleRawText.substring(0, 20) + "...";
            }
            title.setText(titleRawText);
            date.setText(note.getDateTimeFormatted(getContext()));
            if (note.getmContent().length()>30) {
                content.setText(note.getmContent().substring(0,30));
            }else {
                content.setText(note.getmContent());
            }
        }
        return convertView;
    }
}