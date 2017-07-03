//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package benchmarks.testcases;

public class TestAtomicity9 {
    public static int a = 0;
    public static int b = 0;
    public static int c = 0;
    public static int d = 0;
    public static int e = 0;
    public static int f = 0;
    public static int g = 0;
    public static int h = 0;
    public static int i = 0;
    public static final Object lock1 = new Object();
    public static final Object lock2 = new Object();
    public static final Object lock3 = new Object();

    public TestAtomicity9() {
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread() {
            public void run() {
                ++TestAtomicity9.a;
                ++TestAtomicity9.b;
                ++TestAtomicity9.f;
                ++TestAtomicity9.g;
            }
        };
        Thread t2 = new Thread() {
            public void run() {
                ++TestAtomicity9.c;
                ++TestAtomicity9.d;
                ++TestAtomicity9.g;
                ++TestAtomicity9.h;
            }
        };
        Thread t3 = new Thread() {
            public void run() {
                ++TestAtomicity9.d;
                ++TestAtomicity9.e;
                ++TestAtomicity9.h;
                ++TestAtomicity9.i;
            }
        };
        t1.start();
        t2.start();
        t3.start();
        Object var4 = lock1;
        synchronized(lock1) {
            int foo = a;
            foo = b;
            foo = c;
            Object var6 = lock2;
            synchronized(lock2) {
                Object var7 = lock3;
                synchronized(lock3) {
                    foo = f;
                    foo = i;
                }

                foo = c;
                foo = e;
            }
        }

        t1.join();
        t2.join();
        t3.join();
    }
}
