package org.apache.log4j.helpers;

import org.apache.log4j.ConsoleAppender;

import edu.illinois.jacontebe.framework.Reporter;

/**
 * Bug URL: https://issues.apache.org/bugzilla/show_bug.cgi?id=54325 
 * This is a race.
 * Reproduce environment: log4j 1.2.13, JDK 1.6.0_33
 * 
 * @author Ziyi Lin
 */
public class Test54325 {

    private AppenderAttachableImpl aai;
    private static final int DEFAULT_THREADS = 4;
    private static boolean buggy;

    public Test54325() {
        aai = new AppenderAttachableImpl();
        for (int i = 0; i < 1000; i++) {
            aai.addAppender(new ConsoleAppender());
        }
        buggy = false;
    }

    public void run() {
        Thread[] ts = new Thread[DEFAULT_THREADS];
        for (int i = 0; i < DEFAULT_THREADS; i++) {
            ts[i] = new RemoveThread();
            ts[i].start();
        }
        for (int i = 0; i < DEFAULT_THREADS; i++) {
            try {
                ts[i].join();
            } catch (InterruptedException e) {

            }
        }
    }

    private class RemoveThread extends Thread {
        public void run() {
            try {
                aai.removeAllAppenders();
            } catch (ArrayIndexOutOfBoundsException e) {
                buggy = true;
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Reporter.reportStart("log4j54325", 0, "race");
        Test54325 test = new Test54325();
        test.run();
        Reporter.reportEnd(buggy);
    }
}