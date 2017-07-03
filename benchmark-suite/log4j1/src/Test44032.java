import org.apache.log4j.spi.ThrowableInformation;

import edu.illinois.jacontebe.OptionHelper;
import edu.illinois.jacontebe.framework.Reporter;

/**
 * Bug URL:https://issues.apache.org/bugzilla/show_bug.cgi?id=44032
 * This is a race.
 * Reproduce environment: log4j 1.2.15, JDK 1.6.0_33
 * 
 * Options:
 * --threadnum, -tn: number of threads, deafault is 50.
 * --loops, -l:      number of iterations, default is 30.
 * 
 * @author Ziyi Lin
 */
public class Test44032 {

    private static final int DEFAULT_LOOPS = 30;

    private static final int DEFAULT_THREAD_NUM = 50;
    
    private ThrowableInformation ti;
    private static volatile boolean buggy;

    public Test44032() {
        ti = new ThrowableInformation(new Throwable());
        buggy = false;
    }

    public void run() {
        int threadsNum = OptionHelper.getThreadNumValue(DEFAULT_THREAD_NUM);
        // Threads to run the buggy method
        Thread[] ts = new Thread[threadsNum];
        for (int i = 0; i < threadsNum; i++) {
            ts[i] = new TestThread();
            ts[i].start();
        }

        for (int i = 0; i < threadsNum; i++) {
            try {
                ts[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class TestThread extends Thread {

        public void run() {
            String[] rt = ti.getThrowableStrRep();
            // Check if elements returned by buggy method is null.
            // It has probability to be null at buggy version.
            for (String s : rt) {
                if (s == null) {
                    buggy = true;
                    throw new NullPointerException();
                }
            }
        }
    }

    public static void main(String[] args) {
        Reporter.reportStart("log4j44032", 0, "race");
        if(!OptionHelper.optionParse(args)){
            return;
        }
        int loops=OptionHelper.getLoopsValue(DEFAULT_LOOPS);
        for (int i = 0; i < loops; i++) {
            Test44032 test = new Test44032();
            test.run();
            if (buggy) {
                break;
            }
        }
        Reporter.reportEnd(buggy);
    }
}