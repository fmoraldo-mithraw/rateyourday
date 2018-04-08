package com.mithraw.howwasyourday.Tools;

public class MathTool {
    public static int floorMod(int value, int mod) {
        int ret = value%mod;
        if (ret < 0) ret += mod;
        return ret;
    }
}
