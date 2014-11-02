package hu.teteny.labor.rfidauthenticatorserver;

import java.net.NetworkInterface;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Enumeration;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest
        extends TestCase {

    public AppTest(String testName) {

    }

    public static void main(String[] args) {
        AppTest.createTestTables("test.db");
        AppTest.writeTestDataToTables("test.db");
    }

    public static void createTestTables(String dbname) {
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + dbname);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            String sql = "CREATE TABLE access "
                    + "(ID INTEGER  PRIMARY KEY     AUTOINCREMENT,"
                    + " code           varchar(255)    NOT NULL, "
                    + " device         varchar(255)     NOT NULL, "
                    + " start          datetime DEFAULT (datetime('now')), "
                    + " end             datetime DEFAULT (datetime('now','+1 days')))";
            stmt.executeUpdate(sql);

            sql = "CREATE TABLE access_log "
                    + "(ID INTEGER  PRIMARY KEY     AUTOINCREMENT,"
                    + " code           varchar(255)    NOT NULL, "
                    + " device         varchar(255)     NOT NULL, "
                    + " pass           int, "
                    + " date             datetime DEFAULT (datetime('now')))";
            stmt.executeUpdate(sql);

            sql = "CREATE TABLE device "
                    + "(ID INTEGER  PRIMARY KEY     AUTOINCREMENT,"
                    + " macadd           varchar(255)    NOT NULL, "
                    + " device         varchar(255)     NOT NULL)";

            stmt.executeUpdate(sql);

            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("Tables created successfully");
    }

    public static void writeTestDataToTables(String dbname) {
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + dbname);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
            int j = 1;
            while (networks.hasMoreElements()) {
                NetworkInterface network = networks.nextElement();
                byte[] mac = network.getHardwareAddress();
                if (mac != null) {
                    String macadd = "";
                    for (int i = 0; i < mac.length; i++) {
                        macadd += String.format("%02X%s", mac[i], (i < mac.length - 1) ? ":" : "");
                    }
                    String sql = "INSERT INTO device (macadd,device) VALUES ('" + macadd + "','test" + j + "')";
                    stmt.executeUpdate(sql);
                    j++;
                }
            }

            for (int i = 0; i < j; i++) {
                String sql = "INSERT INTO access (code,device) VALUES ('*','test" + i + "')";
                stmt.executeUpdate(sql);
            }

            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("Tables created successfully");
    }
}
