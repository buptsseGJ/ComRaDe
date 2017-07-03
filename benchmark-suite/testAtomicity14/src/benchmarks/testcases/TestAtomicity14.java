//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package benchmarks.testcases;

public class TestAtomicity14 {
    public static int a = 0;
    public static int b = 0;
    public static int c = 0;
    public static int d = 0;
    public static int e = 0;

    public TestAtomicity14() {
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread() {
            public void run() {
                TestAtomicity14.c = 42;
                TestAtomicity14.d = 42;
                TestAtomicity14.a = 42;
                TestAtomicity14.e = 42;
            }
        };
        Thread t2 = new Thread() {
            public void run() {
                TestAtomicity14.e = 42;
                TestAtomicity14.c = 42;
            }
        };
        t1.start();
        t2.start();
        f();
        t1.join();
        t2.join();
    }

    private static void f() {
        a = 42;
        d = 42;
    }
}
