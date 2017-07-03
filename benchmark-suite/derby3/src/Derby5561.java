import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;

import org.apache.derby.client.ClientPooledConnection;
import org.apache.derby.client.am.Connection;
import org.apache.derby.client.am.LogicalConnection;
import org.apache.derby.client.am.SqlException;

import edu.illinois.jacontebe.framework.Reporter;

/**
 * Bug URL:https://issues.apache.org/jira/browse/DERBY-5561 
 * This is a race.
 * Reproduce environment: 10.5.1.1, JDK 1.6.0_33
 * A NullPointerException is expected. 
 * 
 * @author Ziyi Lin
 * 
 */
public class Derby5561 {

    private static class NativeRunnable implements Runnable {
        public void run() {
            try {
                latch.countDown();
                logicalConnection.nativeSQL(sql);

            } catch (SQLException e) {
                System.out.println("sql exception.");
                e.printStackTrace();
            } catch (NullPointerException e) {
                buggy = true;
                throw e;
            }
        }
    }

    private static class NullRunnable implements Runnable {
        public void run() {
            try {
                latch.await();

            } catch (InterruptedException e) {

                e.printStackTrace();
            }

            logicalConnection.nullPhysicalConnection();
            endLatch.countDown();
        }
    }

    /**
     * Inherits from LogicalConnection. The only difference in this class is two
     * CountDownLatch instances are added to enforce the interleaving. So the
     * expected exception would be thrown in one run.
     * 
     * @author Ziyi Lin
     * 
     */
    private static class TestConnection extends LogicalConnection {

        public TestConnection(Connection physicalConnection,
                ClientPooledConnection pooledConnection) throws SqlException {
            super(physicalConnection, pooledConnection);

        }

        public String nativeSQL(String sql) throws SQLException {
            try {
                checkForNullPhysicalConnection();
                latch.countDown();
                try {
                    endLatch.await();
                } catch (InterruptedException e) {

                }
                return physicalConnection_.nativeSQL(sql);
            } catch (SQLException sqle) {

                throw sqle;
            }
        }

    }

    private static LogicalConnection logicalConnection;
    private static boolean buggy;
    static final String sql = "sql";

    static final String ret = "ret";

    private static CountDownLatch latch = new CountDownLatch(1);

    private static CountDownLatch endLatch = new CountDownLatch(1);

    public static void main(String[] args) throws SqlException, SQLException {
        Reporter.reportStart("derby5561", 0, "race");
        // Prepare mock methods and instances to make sure
        // the program goes along the expected path.
        Connection physicalConnection = mock(Connection.class);
        when(physicalConnection.nativeSQL(sql)).thenReturn(ret);
        ClientPooledConnection pooledConnection = mock(ClientPooledConnection.class);
        logicalConnection = new TestConnection(physicalConnection,
                pooledConnection);
        buggy = false;
        try {
            run();
        } catch (Exception e) {

        }
        Reporter.reportEnd(buggy);
    }

    private static void run() throws InterruptedException {
        Thread t1 = new Thread(new NullRunnable());
        Thread t2 = new Thread(new NativeRunnable());

        t1.start();
        t2.start();

        t1.join();
        t2.join();
    }
}
