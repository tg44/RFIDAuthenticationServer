/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.teteny.labor.rfidauthenticatorserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author Törcsi
 */
public class DatabaseHandler {

    private static DatabaseHandler singleton;
    Connection c = null;

    private DatabaseHandler(String dbname) {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + dbname);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public static DatabaseHandler getInstance(String dbname) {
        if (singleton != null) {
            throw new RuntimeException("The handler is initialized before!");
        } else {
            singleton = new DatabaseHandler(dbname);
            return singleton;
        }
    }

    public static DatabaseHandler getInstance() {
        if (singleton == null) {
            throw new RuntimeException("The handler is not initialized yet!");
        } else {
            return singleton;
        }
    }

    private final String sqlSelectAccess = "SELECT * FROM access WHERE end>=datetime('now')";
    private final String sqlInsertAccessLog = "INSERT INTO access_log (code,device,pass,date) VALUES (?,?,?,datetime('now' ))";
    private final String sqlSelectDevices = "SELECT * FROM device WHERE macadd=?";

    public boolean tryToLogin(String code, String device) {
        try {
            //can get access?
            boolean authenticated = false;
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(sqlSelectAccess);
            while (rs.next()) {
                if (rs.getString("device").equals(device)) {
                    if (rs.getString("code").equals("*") || rs.getString("code").equals(code)) {
                        authenticated = true;
                    }
                }
            }
            //log the try
            PreparedStatement prepStatement = c.prepareStatement(sqlInsertAccessLog);
            prepStatement.setString(1, code);
            prepStatement.setString(2, device);
            prepStatement.setInt(3, authenticated ? 1 : 0);
            prepStatement.executeUpdate();

            return authenticated;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return false;
    }

    public String getDeviceCode(String macAdd) {
        try {
            PreparedStatement prepStatement = c.prepareStatement(sqlSelectDevices);
            prepStatement.setString(1, macAdd);
            ResultSet rs = prepStatement.executeQuery();
            while (rs.next()) {
                return rs.getString("device");
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return null;
    }
}
