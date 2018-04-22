package com.mithraw.howwasyourday.Tools;

public class ImportResult {
    int fail;
    int succeed;

    public int getTotal() {
        return fail + succeed;
    }

    public int getFail() {
        return fail;
    }

    public void setFail(int fail) {
        this.fail = fail;
    }

    public int getSucceed() {
        return succeed;
    }

    public void setSucceed(int succed) {
        this.succeed = succed;
    }

    public ImportResult(int fail, int succeed) {
        this.fail = fail;
        this.succeed = succeed;
    }
}
