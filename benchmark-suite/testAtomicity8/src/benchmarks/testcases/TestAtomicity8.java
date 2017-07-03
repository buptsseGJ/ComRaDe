//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package benchmarks.testcases;

public class TestAtomicity8 {
    public static int x = 0;
    public static int y = 0;
    public static int z = 0;
    public static final Object lock = new Object();

    public TestAtomicity8() {
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread() {
            public void run() {
                ++TestAtomicity8.x;
                ++TestAtomicity8.y;
            }
        };
        Thread t2 = new Thread() {
            public void run() {
                ++TestAtomicity8.y;
                ++TestAtomicity8.z;
            }
        };
        t1.start();
        t2.start();
        Object var3 = lock;
        synchronized(lock) {
            int foo = x;
            foo = z;
        }

        t1.join();
        t2.join();
    }
}
