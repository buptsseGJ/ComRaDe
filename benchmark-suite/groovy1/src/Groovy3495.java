import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.codehaus.groovy.tools.RootLoader;

import edu.illinois.jacontebe.OptionHelper;
import edu.illinois.jacontebe.framework.Reporter;

/**
 * Bug URL:http://jira.codehaus.org/browse/GROOVY-3495
 * This is a race
 * Reproduce environment: groovy 1.7.9 JDK 1.6.0_33.
 * 
 * Arguments: --threadnum, -tn :thread number , default value is 3
 * 
 * When two threads try to load the same class at the same time without
 * synchronization, a LinkageError throws.
 * 
 * Exception in thread "Thread-0" java.lang.LinkageError: loader (instance of
 * org/codehaus/groovy/tools/RootLoader): attempted duplicate class definition
 * for name: "groovy/beans/Bindable" at
 * java.lang.ClassLoader.defineClass1(Native Method) at
 * java.lang.ClassLoader.defineClassCond(ClassLoader.java:631) at
 * java.lang.ClassLoader.defineClass(ClassLoader.java:615) at
 * java.security.SecureClassLoader.defineClass(SecureClassLoader.java:141) at
 * java.net.URLClassLoader.defineClass(URLClassLoader.java:283) at
 * java.net.URLClassLoader.access$000(URLClassLoader.java:58) at
 * java.net.URLClassLoader$1.run(URLClassLoader.java:197) at
 * java.security.AccessController.doPrivileged(Native Method) at
 * java.net.URLClassLoader.findClass(URLClassLoader.java:190) at
 * org.codehaus.groovy.tools.RootLoader.oldFindClass(RootLoader.java:152) at
 * org.codehaus.groovy.tools.RootLoader.loadClass(RootLoader.java:124) at
 * java.lang.ClassLoader.loadClass(ClassLoader.java:247) at
 * org.codehaus.groovy.tools.Groovy3495$1.run(Groovy3495.java:34)
 * 
 * @author Ziyi Lin
 * 
 */
public class Groovy3495 {

    private RootLoader rootLoader;
    private final static int DEFAULT_THREAD_SIZE = 3;
    private int threadNumber;
    private boolean buggy = false;

    public int getThreadNumber() {
        return threadNumber;
    }

    public void setThreadNumber(int threadNumber) {
        this.threadNumber = threadNumber;
    }

    public Groovy3495() throws MalformedURLException {

        // Prepare a jar file to load.
        File file = new File(System.getProperty("java.home")
                + "/lib/charsets.jar");
        if (!file.exists()) {
            System.out.println("declared jar file:"+ file.getAbsolutePath() +" does not exist");
        }
        URL[] classpath = new URL[] { new URL("jar:" + file.toURI().toURL()
                + "!/") };

        rootLoader = new RootLoader(classpath, this.getClass().getClassLoader());
        threadNumber = DEFAULT_THREAD_SIZE;

    }

    public void run() {

        Thread[] threads = new Thread[threadNumber];
        // start several threads to load the same class by same class loader. So
        // the class loader will load the same class simultaneously
        for (int i = 0; i < threadNumber; i++) {
            threads[i] = new Thread() {
                public void run() {
                    // load a class
                    try {

                        rootLoader.loadClass("sun.awt.HKSCS");

                    } catch (ClassNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (LinkageError e) {
                        buggy = true;
                        e.printStackTrace();
                    }

                }
            };
            threads[i].start();
        }
        for (int i = 0; i < threadNumber; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) throws MalformedURLException,
            ClassNotFoundException {
        Reporter.reportStart("groovy3495", 0, "race");
        if (!OptionHelper.optionParse(args)) {
            return;
        }
        int threadNumber = OptionHelper.getThreadNumValue(DEFAULT_THREAD_SIZE);

        Groovy3495 test = new Groovy3495();
        test.setThreadNumber(threadNumber);
        test.run();
        Reporter.reportEnd(test.buggy);
    }
}
