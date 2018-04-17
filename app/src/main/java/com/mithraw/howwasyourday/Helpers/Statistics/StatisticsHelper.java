package com.mithraw.howwasyourday.Helpers.Statistics;

/*
Format the final string of a stat
 */
public class StatisticsHelper {
    static public String finalFormat(String s, float rate, int precision) {
        String floatFormat;
        floatFormat = "%." + precision + "f";
        return (s + " (" + String.format(floatFormat, rate) + "/5)");
    }
    static public String floatFormat(float rate, int precision) {
        if(rate == 0)
            return "";
        String floatFormat;
        floatFormat = "%." + precision + "f";
        return (String.format(floatFormat, rate));
    }
}
