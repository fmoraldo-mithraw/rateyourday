package com.mithraw.howwasyourday.Helpers.Statistics;

import com.mithraw.howwasyourday.databases.Day;

public class StatisticsDatas {
    private float rate = 0;
    private int id = 0;


    public StatisticsDatas() {
    }
    public StatisticsDatas(Day day) {

    }

    public float getRate() {
        return rate;
    }

    public StatisticsDatas setRate(float rate) {
        this.rate = rate;
        return this;
    }

    public int getId() {
        return id;
    }

    public StatisticsDatas setId(int id) {
        this.id = id;
        return this;
    }
}