package cf.wynntils.core.utils;

import cf.wynntils.ModCore;

public class NanoTracker {
    private static long start = 0;

    public static void start() {
        if(!ModCore.DEBUG) return;
        start = System.nanoTime();
    }

    public static void report(boolean shouldStart) {
        if(!ModCore.DEBUG) return;
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        System.out.println((System.nanoTime() - start) + "ns reported by: " + stackTraceElements[2].toString());
        if(shouldStart) start();
    }

}
