package com.mithraw.howwasyourday.Helpers.Statistics;

public class StatisticsHelper {
    String finalFormat(String s, float rate, int precision) {
        String floatFormat;
        floatFormat = "%." + precision + "f";
        return (s + " (" + String.format(floatFormat, rate) + "/5)");
    }
}
