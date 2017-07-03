//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package benchmarks.testcases;

public class TestAtomicity13 {
    public static int x = 0;
    public static int y = 0;

    public TestAtomicity13() {
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread() {
            public void run() {
                ++TestAtomicity13.x;
            }
        };
        Thread t2 = new Thread() {
            public void run() {
                ++TestAtomicity13.y;
            }
        };
        t1.start();
        t2.start();
        g();
        f();
        t1.join();
        t2.join();
        System.out.println("x = " + x);
    }

    private static void f() {
        x = 42;
        y = 42;
    }

    private static void g() {
        ++x;
    }
}
