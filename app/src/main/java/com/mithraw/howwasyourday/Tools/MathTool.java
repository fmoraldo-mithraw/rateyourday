package com.mithraw.howwasyourday.Tools;

/*
Do the true mod(%)
 */
public class MathTool {
    public static int floorMod(int value, int mod) {
        int ret = value%mod;
        if (ret < 0) ret += mod;
        return ret;
    }
}
