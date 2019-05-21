package com.example.note;

import android.content.Context;
import android.provider.ContactsContract;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class Utilities {

    public static final String FILE_EXTENTION = ".bin";

    public static boolean saveNote (Context context, Note note){
    String fileName = String.valueOf(note.getDateTime()) + FILE_EXTENTION;

        FileOutputStream fos;
        ObjectOutputStream oos;

        try {
            fos = context.openFileOutput(fileName, context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(note);
            oos.close();
            oos.close();
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }

    return true;
    }
}
