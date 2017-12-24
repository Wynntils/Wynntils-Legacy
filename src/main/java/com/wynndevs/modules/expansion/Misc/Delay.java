package com.wynndevs.modules.expansion.Misc;


public class Delay {

    long cur = System.currentTimeMillis();
    long end;
    int delay;
    boolean resetting = false;

    public Delay(float delay){
        this(delay,false);
    }

    public Delay(float delay,boolean resetting){
        this.cur = System.currentTimeMillis();
        this.delay = Math.round(delay * 1000);
        this.end = cur;
        this.resetting = resetting;
    }

    public void Reset(){
        this.cur = System.currentTimeMillis();
        this.end = cur + this.delay;
        passedFlag = false;
    }

    public boolean Passed() {
        this.cur = System.currentTimeMillis();
        boolean passed = (cur > end);

        if(passed && this.resetting) this.Reset();

        passedFlag = true;

        return (passed);
    }
    boolean passedFlag = false;
    public boolean PassedOnce() {
        this.cur = System.currentTimeMillis();
        boolean passed = (cur > end);

        if(passed && this.resetting) this.Reset();

        boolean passed2 = (passed && !passedFlag);
        passedFlag = true;
        return passed2;
    }

    public float getSecondsLeft(){
        this.cur = System.currentTimeMillis();

        return (float)((this.end - this.cur) * 0.001);
    }
}
