package com.mithraw.howwasyourday.Helpers.Statistics;

import android.content.res.Resources;

import com.mithraw.howwasyourday.App;
import com.mithraw.howwasyourday.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
/*
Class used in the funny stat activity
Use it like a builder :
StatisticsAdds adds = new StatisticsAdds()
adds
                        .setNumDayRated1(1)
                        .setNumDayRated2(1)
                        .setNumDayRated3(1)
                        .setNumDayRated4(1)
                        .setNumDayRated5(1)
                        .setAverageDay(3.1)
                        .setNumberOfRatedDays(56)
                        .setAvgMonday(2.2);
                        .setAvgTuesday(2.2);
                        .setAvgWednesday(2.2);
                        .setAvgThursday(2.2);
                        .setAvgFriday(2.2);
                        .setAvgSaturday(2.2);
                        .setAvgSunday(2.2);
                        .init();

 */

public class StatisticsAdds {
    boolean isInit = false;
    List<String> listExtremQuote;
    List<String> listBigGapQuote;
    List<String> listBadAvgQuote;
    String extremQuote = "";
    String bigGapQuote = "";
    String badAvgQuote = "";
    int numDayRated1, numDayRated2, numDayRated3, numDayRated4, numDayRated5;
    float averageDay;
    int numberOfRatedDays;
    float avgMonday, avgTuesday, avgWednesday, avgThursday, avgFriday, avgSaturday, avgSunday;
    Map<Integer,Float> mapAvgDays;

    public StatisticsAdds(){
        Resources res = App.getContext().getResources();
        listExtremQuote = new ArrayList<>();
        listExtremQuote.add(res.getString(R.string.extreme_quote1));
        listBigGapQuote= new ArrayList<>();
        listBigGapQuote.add(res.getString(R.string.big_gap1));
        listBadAvgQuote= new ArrayList<>();
        listBadAvgQuote.add(res.getString(R.string.bad_avg1));
        mapAvgDays = new HashMap<>();

    }
    public StatisticsAdds init(){
        if(avgMonday != 0)
        mapAvgDays.put(R.string.monday_full, avgMonday);
        if(avgTuesday != 0)
        mapAvgDays.put(R.string.tuesday_full, avgTuesday);
        if(avgWednesday != 0)
        mapAvgDays.put(R.string.wednesday_full, avgWednesday);
        if(avgThursday != 0)
        mapAvgDays.put(R.string.thursday_full, avgThursday);
        if(avgFriday != 0)
        mapAvgDays.put(R.string.friday_full, avgFriday);
        if(avgSaturday != 0)
        mapAvgDays.put(R.string.saturday_full, avgSaturday);
        if(avgSunday != 0)
        mapAvgDays.put(R.string.sunday_full, avgSunday);
        isInit = true;
        getExtremQuote();
        getBigGapQuote();
        getBadAvgQuote();
        return this;
    }
    private Map.Entry<Integer, Float> getWorstDay(){
        Map.Entry<Integer, Float> min = null;
        for (Map.Entry<Integer, Float> entry : mapAvgDays.entrySet()) {
            if (min == null || min.getValue() > entry.getValue()) {
                min = entry;
            }
        }
        return min;
    }
    private Map.Entry<Integer, Float> getBestDay(){
        Map.Entry<Integer, Float> max = null;
        for (Map.Entry<Integer, Float> entry : mapAvgDays.entrySet()) {
            if (max == null || max.getValue() < entry.getValue()) {
                max = entry;
            }
        }
        return max;
    }
    private void checkInit() throws IllegalStateException {
        if(!isInit) throw new IllegalStateException("You must call init() before retrieving any data from this class");
    }
    public String getExtremQuote() throws IllegalStateException{
        if(extremQuote.equals("")) {
            checkInit();
            if ((numDayRated1 + numDayRated5 > numDayRated2 + numDayRated3 + numDayRated4)&&(numberOfRatedDays>50)) {
                if(listExtremQuote.size()>1) {
                    Random r = new Random();
                    extremQuote = listExtremQuote.get(r.nextInt(listExtremQuote.size()) - 1);
                }else {
                    extremQuote = listExtremQuote.get(0);
                }
            }
        }
        return extremQuote;
    }
    public String getBigGapQuote() throws IllegalStateException{
        if(bigGapQuote.equals("")) {
            checkInit();
            Map.Entry<Integer, Float> min = getWorstDay();
            Map.Entry<Integer, Float> max = getBestDay();
            if ((min != null) && (max != null)) {
                if ((max.getValue() > (min.getValue() + 1.5)) && (numberOfRatedDays > 50)) {
                    if (listBigGapQuote.size() > 1) {
                        Random r = new Random();
                        bigGapQuote = listBigGapQuote.get(r.nextInt(listBigGapQuote.size()) - 1);
                    } else {
                        bigGapQuote = listBigGapQuote.get(0);
                    }
                }
                Resources res = App.getContext().getResources();
                bigGapQuote = bigGapQuote.replace("{worst_day}", res.getString(min.getKey())).replace("{best_day}", res.getString(max.getKey()));
            }
        }
        return bigGapQuote;
    }
    public String getBadAvgQuote() throws IllegalStateException{
        if(badAvgQuote.equals("")) {
            checkInit();
            if ((averageDay < 3)&&(numberOfRatedDays>50)) {
                if(listBadAvgQuote.size()>1) {
                    Random r = new Random();
                    badAvgQuote = listBadAvgQuote.get(r.nextInt(listBadAvgQuote.size()) - 1);
                }else {
                    badAvgQuote = listBadAvgQuote.get(0);
                }
            }
        }
        return badAvgQuote;
    }


    public int getNumDayRated1() throws IllegalStateException{
        checkInit();
        return numDayRated1;
    }

    public StatisticsAdds setNumDayRated1(int numDayRated1) {
        this.numDayRated1 = numDayRated1;
        return this;
    }

    public int getNumDayRated2() throws IllegalStateException{
        checkInit();
        return numDayRated2;
    }

    public StatisticsAdds setNumDayRated2(int numDayRated2) {
        this.numDayRated2 = numDayRated2;
        return this;
    }

    public int getNumDayRated3() throws IllegalStateException{
        checkInit();
        return numDayRated3;
    }

    public StatisticsAdds setNumDayRated3(int numDayRated3) {
        this.numDayRated3 = numDayRated3;
        return this;
    }

    public int getNumDayRated4() throws IllegalStateException{
        checkInit();
        return numDayRated4;
    }

    public StatisticsAdds setNumDayRated4(int numDayRated4) {
        this.numDayRated4 = numDayRated4;
        return this;
    }

    public int getNumDayRated5() throws IllegalStateException{
        checkInit();
        return numDayRated5;
    }

    public StatisticsAdds setNumDayRated5(int numDayRated5) {
        this.numDayRated5 = numDayRated5;
        return this;
    }

    public float getAverageDay() throws IllegalStateException{
        checkInit();
        return averageDay;
    }

    public StatisticsAdds setAverageDay(float averageDay) {
        this.averageDay = averageDay;
        return this;
    }

    public int getNumberOfRatedDays() throws IllegalStateException{
        checkInit();
        return numberOfRatedDays;
    }

    public StatisticsAdds setNumberOfRatedDays(int numberOfRatedDays) {
        this.numberOfRatedDays = numberOfRatedDays;
        return this;
    }

    public float getAvgMonday() throws IllegalStateException{
        checkInit();
        return avgMonday;
    }

    public StatisticsAdds setAvgMonday(float avgMonday) {
        this.avgMonday = avgMonday;
        return this;
    }

    public float getAvgTuesday() throws IllegalStateException{
        checkInit();
        return avgTuesday;
    }

    public StatisticsAdds setAvgTuesday(float avgTuesday) {
        this.avgTuesday = avgTuesday;
        return this;
    }

    public float getAvgWednesday() throws IllegalStateException{
        checkInit();
        return avgWednesday;
    }

    public StatisticsAdds setAvgWednesday(float avgWednesday) {
        this.avgWednesday = avgWednesday;
        return this;
    }

    public float getAvgThursday() throws IllegalStateException{
        checkInit();
        return avgThursday;
    }

    public StatisticsAdds setAvgThursday(float avgThursday) {
        this.avgThursday = avgThursday;
        return this;
    }

    public float getAvgFriday() throws IllegalStateException{
        checkInit();
        return avgFriday;
    }

    public StatisticsAdds setAvgFriday(float avgFriday) {
        this.avgFriday = avgFriday;
        return this;
    }

    public float getAvgSaturday() throws IllegalStateException{
        checkInit();
        return avgSaturday;
    }

    public StatisticsAdds setAvgSaturday(float avgSaturday) {
        this.avgSaturday = avgSaturday;
        return this;
    }

    public float getAvgSunday() throws IllegalStateException{
        checkInit();
        return avgSunday;
    }

    public StatisticsAdds setAvgSunday(float avgSunday) {
        this.avgSunday = avgSunday;
        return this;
    }


}
