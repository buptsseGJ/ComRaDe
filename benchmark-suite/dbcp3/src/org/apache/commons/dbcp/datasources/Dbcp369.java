package org.apache.commons.dbcp.datasources;

import static org.mockito.Mockito.mock;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import edu.illinois.jacontebe.OptionHelper;
import edu.illinois.jacontebe.framework.Reporter;

/**
 * <p>
 * Bug URL: https://issues.apache.org/jira/browse/DBCP-369 <br>
 * Implemented Version: dbcp 1.2, JDK 1.6.0_33
 * </p>
 * <p>
 * This is a race. <br>
 * Refer to 369.html in description directory for more information.
 * </p>
 * 
 * @author Ziyi Lin
 * 
 */
public class Dbcp369 {

    private volatile static boolean buggy = false;
    private static final int DEFAULT_LOOPS = 10;

    private static class Datasource {
        private String name;

        private InstanceKeyDataSource ids;

        public Datasource() {
            ids = mock(InstanceKeyDataSource.class);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public InstanceKeyDataSource getIds() {
            return ids;
        }

    }

    public static void main(String[] args) throws SQLException {
        Reporter.reportStart("dbcp369", 0, "race");
        if (!OptionHelper.optionParse(args)) {
            return;
        }
        int loops = OptionHelper.getLoopsValue(DEFAULT_LOOPS);

        // Prepare initial data.
        final ArrayList<Datasource> dataSources = new ArrayList<Datasource>();
        for (int j = 0; j < 100; j++) {
            Datasource dataSource = new Datasource();
            dataSources.add(dataSource);
        }

        // Two threads access one HashMap instance at the same time, and one of
        // them modifies the HashMap.
        // This will cause an exception.
        for (int i = 0; i < loops; i++) {
            if (buggy) {
                break;
            }
            final CountDownLatch latch = new CountDownLatch(1);
            Thread t1 = new Thread(new Runnable() {
                public void run() {
                    try {
                        latch.countDown();
                        for (Datasource dataSource : dataSources) {
                            String key = InstanceKeyObjectFactory
                                    .registerNewInstance(dataSource.getIds());
                            dataSource.setName(key);
                        }
                    } catch (Exception e) {
                        buggy = true;
                        e.printStackTrace();
                    }
                }
            });

            Thread t2 = new Thread(new Runnable() {
                public void run() {
                    try {
                        latch.await();
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    for (Datasource dataSource : dataSources) {
                        try {
                            String key = dataSource.getName();
                            InstanceKeyObjectFactory.removeInstance(key);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            t1.start();
            t2.start();
            try {
                t1.join();
                t2.join();
            } catch (InterruptedException ie) {
                // ignore
            }
        }
        Reporter.reportEnd(buggy);
    }
}