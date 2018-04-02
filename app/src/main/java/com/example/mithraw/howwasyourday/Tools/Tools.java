package com.example.mithraw.howwasyourday.Tools;

public class Tools {
    public static boolean isHour(String myString) {
        String[] timeParts = myString.split(":");
        if ((timeParts.length == 2) &&
                (timeParts[0].length() < 3) &&
                (timeParts[1].length() < 3) &&
                (timeParts[0].length() > 0) &&
                (timeParts[1].length() > 1) &&
                (isNumber(timeParts[0])) &&
                (isNumber(timeParts[0]))) {
            return true;
        }
        return false;
    }

    private static boolean isNumber(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
