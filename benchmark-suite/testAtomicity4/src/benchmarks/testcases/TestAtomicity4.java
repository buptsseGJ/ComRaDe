//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package benchmarks.testcases;

public class TestAtomicity4 {
    public static int x = 0;
    public static final Object lock = new Object();
    public static boolean cond = true;

    public TestAtomicity4() {
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread() {
            public void run() {
                Object var1 = TestAtomicity4.lock;
                synchronized(TestAtomicity4.lock) {
                    TestAtomicity4.lock.notify();
                    TestAtomicity4.cond = false;
                }

                ++TestAtomicity4.x;
            }
        };
        t1.start();
        Thread.sleep(10L);
        Object var2 = lock;
        synchronized(lock) {
            if(cond) {
                try {
                    lock.wait();
                } catch (InterruptedException var7) {
                    var7.printStackTrace();
                }
            }
        }

        var2 = lock;
        synchronized(lock) {
            ++x;
            ++x;
        }

        t1.join();
        System.out.println("x = " + x);
    }
}
