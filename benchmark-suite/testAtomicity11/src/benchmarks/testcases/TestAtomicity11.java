//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package benchmarks.testcases;

public class TestAtomicity11 {
    public static int x = 0;
    public static final Object lock = new Object();

    public TestAtomicity11() {
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread() {
            public void run() {
                ++TestAtomicity11.x;
            }
        };
        t.start();
        f();
        t.join();
        System.out.println("x = " + x);
    }

    private static void f() {
        ++x;
        ++x;
    }
}
