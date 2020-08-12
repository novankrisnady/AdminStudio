package com.novankisnady.staffstudioband.Common;

import com.novankisnady.staffstudioband.Model.Room;
import com.novankisnady.staffstudioband.Model.Studio;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Formatter;

public class Common {
    public static final Object DISABLE_TAG = "DISABLE";
    public static final int TOTAL_TIME_SLOT = 24;
    public static final String LOGGED_KEY = "LOGGED";
    public static final String STATE_KEY = "STATE";
    public static final String STUDIO_KEY = "STUDIO";
    public static final String ROOM_KEY = "ROOM";
    public static String state_name="";

    public static Room currentRoom;
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");
    public static Calendar bookingDate = Calendar.getInstance();
    public static Studio selected_studio;

    public static String convertTimeSlotToString(int slot) {
        switch (slot)
        {
            case 0:
                return "01:00-02:00";
            case 1:
                return "02:00-03:00";
            case 2:
                return "03:00-04:00";
            case 3:
                return "04:00-05:00";
            case 4:
                return "05:00-06:00";
            case 5:
                return "06:00-07:00";
            case 6:
                return "07:00-08:00";
            case 7:
                return "08:00-09:00";
            case 8:
                return "09:00-10:00";
            case 9:
                return "10:00-11:00";
            case 10:
                return "11:00-12:00";
            case 11:
                return "12:00-13:00";
            case 12:
                return "13:00-14:00";
            case 13:
                return "14:00-15:00";
            case 14:
                return "15:00-16:00";
            case 15:
                return "16:00-17:00";
            case 16:
                return "17:00-18:00";
            case 17:
                return "18:00-19:00";
            case 18:
                return "19:00-20:00";
            case 19:
                return "20:00-21:00";
            case 20:
                return "21:00-22:00";
            case 21:
                return "22:00-23:00";
            case 22:
                return "23:00-24:00";
            case 23:
                return "24:00-01:00";
            default:
                return "Closed";
        }
    }
}
