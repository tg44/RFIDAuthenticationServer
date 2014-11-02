/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.teteny.labor.rfidauthenticatortestclient;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 *
 * @author Törcsi
 */
public class TestClient {

    public static void main(String[] args) {
        try {
            Socket clientSocket = new Socket("localhost", 4000);
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            Thread.sleep(10000);
            
            outToServer.writeBytes("123456" + '\n');
            String answer = inFromServer.readLine();
            System.out.println("FROM SERVER: " + answer);
            
            Thread.sleep(10000);
            
            outToServer.writeBytes("1234567" + '\n');
            answer = inFromServer.readLine();
            System.out.println("FROM SERVER: " + answer);
            
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
