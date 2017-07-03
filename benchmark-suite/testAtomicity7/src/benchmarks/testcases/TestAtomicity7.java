//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package benchmarks.testcases;

public class TestAtomicity7 {
    public static int x = 0;
    public static final Object lock = new Object();

    public TestAtomicity7() {
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread() {
            public void run() {
                ++TestAtomicity7.x;
            }
        };
        t.start();
        Object var2 = lock;
        synchronized(lock) {
            Object var3 = lock;
            synchronized(lock) {
                ;
            }

            ++x;
            ++x;
        }

        t.join();
        System.out.println("x = " + x);
    }
}
