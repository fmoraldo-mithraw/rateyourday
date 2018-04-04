package com.example.mithraw.howwasyourday.Tools;

public class Hour {
    private String hour;
    private String minute;
    private int intHour;
    private int intMinute;

    public String getHour() {
        return hour;
    }

    public String getMinute() {
        return minute;
    }

    public int getIntHour() {
        return intHour;
    }

    public int getIntMinute() {
        return intMinute;
    }

    public Hour(String strHour) {
        if ((strHour != null)&&(Hour.isHour(strHour))){
            String[] timeParts = strHour.split(":");
            hour = timeParts[0];
            minute = timeParts[1];
            intHour = Integer.parseInt(hour);
            intMinute = Integer.parseInt(minute);
        }else {
            hour = "0";
            minute = "00";
            intHour = 0;
            intMinute = 0;
        }
    }

    public static boolean isHour(String myString) {
        if(myString == null)
            return false;
        String[] timeParts = myString.split(":");
        if ((timeParts.length == 2) &&
                (timeParts[0].length() < 3) &&
                (timeParts[1].length() < 3) &&
                (timeParts[0].length() > 0) &&
                (timeParts[1].length() > 1) &&
                (Tools.isNumber(timeParts[0])) &&
                (Tools.isNumber(timeParts[0]))) {
            return true;
        }
        return false;
    }
}
