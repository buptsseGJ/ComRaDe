//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package benchmarks.testcases;

public class TestAtomicity1 {
    public static int x = 0;
    public static final Object lock = new Object();

    public TestAtomicity1() {
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread() {
            public void run() {
                ++TestAtomicity1.x;
            }
        };
        t.start();
        Object var2 = lock;
        synchronized(lock) {
            ++x;
            ++x;
        }

        t.join();
        System.out.println("x = " + x);
    }
}
