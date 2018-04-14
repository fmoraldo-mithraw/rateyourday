package com.mithraw.howwasyourday.Tools;

/*
Check if it's a number
 */
public class Tools {
    public static boolean isNumber(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
