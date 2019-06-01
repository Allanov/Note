package com.flaterlab.parkingapp.util;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import com.flaterlab.parkingapp.R;
import com.flaterlab.parkingapp.model.ParkingResult;

import java.util.Date;

public class DialogUtils {

    public static void showParkingResultDialog(Context context, ParkingResult result) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_result);
        dialog.setTitle(R.string.dialog_parking_result);
        dialog.setCanceledOnTouchOutside(false);

        TextView polygonName = dialog.findViewById(R.id.tv_polygon_name);
        polygonName.setText(result.getName());

        TextView time = dialog.findViewById(R.id.tv_time);
        time.setText(getTimeFormatFromMilSec(result.getDuration()));

        TextView startDate = dialog.findViewById(R.id.tv_start_date);
        startDate.setText(dateToString(result.getStarted()));

        TextView endDate = dialog.findViewById(R.id.tv_end_date);
        endDate.setText(dateToString(result.getFinished()));

        TextView ok = dialog.findViewById(R.id.tv_ok_btn);
        ok.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private static String getTimeFormatFromMilSec(long ms) {
        long inSec = ms / 1000;
        long hours = inSec / 3600;
        long minutes = inSec / 60;
        long sec = inSec % 60;
        return getInTwoPlaces(hours) + ":" + getInTwoPlaces(minutes) + ":" + getInTwoPlaces(sec);
    }

    public static String dateToString(Date d) {

        return getMonthName(d.getMonth()) +
                " " + d.getDay() + ",  " +
                d.getHours() + ":" + getInTwoPlaces(d.getMinutes()) +
                ",  " + d.getYear();
    }

    private static String getInTwoPlaces(long m) {
        if (m < 10)
            return "0" + m;
        else
            return m + "";
    }

    private static String getMonthName(int i) {
        String month = "";
        switch (i) {
            case 0:
                month = "Jan";
                break;
            case 1:
                month = "Feb";
                break;
            case 2:
                month = "Mar";
                break;
            case 3:
                month = "Apr";  //Jan., Feb., Mar., Apr., Aug., Sept., Oct., Nov. and Dec
                break;
            case 4:
                month = "May";
                break;
            case 5:
                month = "June";
                break;
            case 6:
                month = "July";
                break;
            case 7:
                month = "Aug";
                break;
            case 8:
                month = "Sept";
                break;
            case 9:
                month = "Oct";
                break;
            case 10:
                month = "Nov";
                break;
            case 11:
                month = "Dec";
                break;
            default:
                throw new IllegalArgumentException("No such month!");

        }

        return month;
    }
}
