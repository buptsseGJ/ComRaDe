package groovy.util;

import java.io.InputStream;

import org.apache.tools.ant.Task;

import edu.illinois.jacontebe.OptionHelper;
import edu.illinois.jacontebe.framework.Reporter;

/**
 * Bug URL:http://jira.codehaus.org/browse/GROOVY-6068
 * This is a race.
 * Reproduce environment: groovy 1.7.9 , JDK 1.6.0_33
 * 
 * Options: 
 * --threadnum, -tn, thread number, default value is 2. 
 * --loops, -l, loop number, default value is 10
 * 
 * @author Ziyi Lin
 *
 */

public class Groovy6068 {

    private AntBuilder antBuilder;
    private int threadsNum;
    private int irNum;

    private static final int DEDAULT_THREAD_NUM = 2;
    private static final int DEFAULT_LOOPS_NUM = 10;

    public Groovy6068(int threadNum, int loopsNum) {
        antBuilder = new AntBuilder();
        this.threadsNum = threadNum;
        this.irNum = loopsNum;
    }

    public void run() throws InterruptedException {

        ThreadBuild[] tb = new ThreadBuild[threadsNum];
        for (int i = 0; i < tb.length; i++) {
            tb[i] = new ThreadBuild();
        }
        for (int i = 0; i < tb.length; i++) {
            tb[i].start();
        }
        for (int i = 0; i < tb.length; i++) {
            tb[i].join();
        }
    }

    private class ThreadBuild extends Thread {

        public void run() {
            for (int i = 0; i < irNum; i++) {
                Task t = new TestTask();
                antBuilder.nodeCompleted(null, t);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Reporter.reportStart("groovy6068", 0, "race");
        if (!OptionHelper.optionParse(args)) {
            return;
        }
        InputStream saved = System.in;

        Groovy6068 test = new Groovy6068(
                OptionHelper.getThreadNumValue(DEDAULT_THREAD_NUM),
                OptionHelper.getLoopsValue(DEFAULT_LOOPS_NUM));
        int i = 0;
        while (i < 10) {
            test.run();
            if (!saved.equals(System.in)) {
                Reporter.reportEnd(true);
                throw new RuntimeException("System.in was altered!");
            }
            i++;
        }
        if (i == 10) {
            Reporter.reportEnd(false);
        }
    }
}

class TestTask extends Task {

    public String getTaskName() {
        return "compile";
    }
}
