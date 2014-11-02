/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.teteny.labor.rfidauthenticatorserver;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;

/**
 *
 * @author Törcsi
 */
public class TCPClientThread extends Thread {

    private DataInputStream is = null;
    private PrintStream os = null;
    private Socket clientSocket = null;
    private String deviceCode=null;
    private String macadd="";
    public TCPClientThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public String getMacadd() {
        return macadd;
    }
    public boolean isActive(){
        return !clientSocket.isClosed();
    }
    
    
    
    @Override
    public void run() {
        try {
            DatabaseHandler dh = DatabaseHandler.getInstance();
            is = new DataInputStream(clientSocket.getInputStream());
            os = new PrintStream(clientSocket.getOutputStream());
            

            InetAddress ip = clientSocket.getInetAddress();
            NetworkInterface ni = NetworkInterface.getByInetAddress(ip);
            if (ni != null) {
                byte[] mac = ni.getHardwareAddress();
                for (int i = 0; i < mac.length; i++) {
                    macadd += String.format("%02X%s", mac[i], (i < mac.length - 1) ? ":" : "");
                }
                deviceCode = dh.getDeviceCode(macadd);
            }
            if (deviceCode != null) {
                while (true) {
                    String line;
                    line = is.readLine();
                    //need some more security
                    if (line.equals("")) {
                        break;
                    }
                    boolean success=dh.tryToLogin(line, deviceCode);
                    os.println(success);
                }
            }
            is.close();
            os.close();
            clientSocket.close();
        } catch (IOException e) {
        }
    }

}
