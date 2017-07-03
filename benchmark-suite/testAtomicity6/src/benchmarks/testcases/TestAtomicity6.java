//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package benchmarks.testcases;

public class TestAtomicity6 {
    public static int x = 0;
    public static final Object lock1 = new Object();
    public static final Object lock2 = new Object();
    public static final Object lock3 = new Object();

    public TestAtomicity6() {
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread() {
            public void run() {
                ++TestAtomicity6.x;
            }
        };
        t.start();
        Object var2 = lock1;
        synchronized(lock1) {
            Object var3 = lock2;
            synchronized(lock2) {
                Object var4 = lock3;
                synchronized(lock3) {
                    ++x;
                }
            }
        }

        t.join();
    }
}
