/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.teteny.labor.rfidauthenticatorserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author Törcsi
 */
public class TCPServer extends Thread{

    int portNumber;
    ServerSocket serverSocket;
    ArrayList<TCPClientThread> threads=new ArrayList<TCPClientThread>();

    public TCPServer(int portnumber) {
        this.portNumber = portnumber;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            System.out.println(e);
        }
        Socket clientSocket;
        while (true) {
            try {
                clientSocket = serverSocket.accept();  
                threads.add(new TCPClientThread(clientSocket));
                threads.get(threads.size()-1).start();
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }
}
