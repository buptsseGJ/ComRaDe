//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package benchmarks.testcases;

public class TestAtomicity12 {
    public static int x = 0;
    public static final Object lock = new Object();

    public TestAtomicity12() {
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread() {
            public void run() {
                ++TestAtomicity12.x;
            }
        };
        t.start();
        f();
        t.join();
        System.out.println("x = " + x);
    }

    private static void f() {
        g();
    }

    private static void g() {
        h();
    }

    private static void h() {
        i();
        ++x;
        ++x;
    }

    private static void i() {
    }
}
