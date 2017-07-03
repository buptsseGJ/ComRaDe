package org.codehaus.groovy.ast;

import java.util.ArrayList;
import java.util.Collection;

import edu.illinois.jacontebe.Helpers;
import edu.illinois.jacontebe.OptionHelper;
import edu.illinois.jacontebe.asm.ClassGenerator;
import edu.illinois.jacontebe.framework.Reporter;

/**
 * Bug URL:http://jira.codehaus.org/browse/GROOVY-4292
 * This is a race.
 * Reproduce environment: groovy 1.7.9, JDK 1.6.0_33, JDK 1.7.0 and JDK 1.8.0
 * 
 * Options:
 * --threadnum, -tn    thread number, default value is 4.
 * --loops, -l         loop number, default value is 5
 *  --monitoroff, -mo  Turn endless looping monitor off. When
 * monitor is turned on, it reports the endless looping message and stops the
 * program.
 * 
 * 
 * @author Ziyi Lin
 * 
 */
public class Groovy4292 {

    private static final int DEFAULT_LOOPS = 5;
    private static final int DEFAULT_THREADS = 4;
    private static final int ITEMS_TO_PUT = 1000;

    public static void main(String[] args) {
        int timeout = 30;
        Reporter.reportStart("groovy4292", timeout, "race");
        // Parse arguments
        if (!OptionHelper.optionParse(args)) {
            return;
        }
        //Helpers.startEndlessLoopMonitor(timeout, "WorkerThread");
        int numberOfLoops = OptionHelper.getLoopsValue(DEFAULT_LOOPS);
        int numberOfThreads = OptionHelper.getThreadNumValue(DEFAULT_THREADS);
        for (int loop = 1; loop <= numberOfLoops; loop++) {
            System.err.println("starting next loop " + loop);
            Collection<Class> keys = generateKeys();
            Thread[] threads = new Thread[numberOfThreads];
            for (int i = 0; i < threads.length; i++) {
                threads[i] = new AsyncWeakRef(keys, i);
                threads[i].start();
            }
            System.err.println("started.");
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        // This test is not supposed to reach this line if bug is reproduced.
        // So report fail to reproduce bug here.
       // Reporter.reportEnd(false);
    }

    private static Collection<Class> generateKeys() {
        Collection<Class> keys = new ArrayList<Class>();
        for (int i = 0; i < ITEMS_TO_PUT; i++) {
            Double dk = new Double(10000.0 * Math.random());
            Integer k = new Integer(dk.intValue());
            ClassGenerator generator = new ClassGenerator();
            Class clazz = generator.generateSampleInterface("I" + k);
            keys.add(clazz);
        }
        return keys;
    }

    private static class AsyncWeakRef extends Thread {

        private final Collection<Class> keys;

        AsyncWeakRef(Collection<Class> keys, int counter) {
            super("WorkerThread-" + counter);
            this.keys = keys;
        }

        @Override
        public void run() {
            for (Class clazz : keys) {
                ClassHelper.makeCached(clazz);
            }
        }
    }
}