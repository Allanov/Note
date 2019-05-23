package com.example.note;
import android.content.Context;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Note implements Serializable {
    private long DateTime;
    private String mTitle;
    private String mContent;

    public Note(long dateTime, String mTitle, String mContent) {
        DateTime = dateTime;
        this.mTitle = mTitle;
        this.mContent = mContent;
    }
    public void setDateTime(long dateTime) {
        DateTime = dateTime;
    }
    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }
    public void setmContent(String mContent) {
        this.mContent = mContent;
    }
    public long getDateTime() {
        return DateTime;
    }
    public String getmTitle() {
        return mTitle;
    }
    public String getmContent() {
        return mContent;
    }
    public String getDateTimeFormatted(Context context){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:MM", context.getResources().getConfiguration().locale);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(new Date(DateTime));
    }
}