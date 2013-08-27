/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.zyclonite.testing;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author zyclonite
 */
public class HttpMirror {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(Integer.parseInt(args[0]));
            System.out.println("HttpMirror v1.1 - by zyclonite networx");
            while (true) {
                Socket connectionSocket = serverSocket.accept();
                httpThread thr = new httpThread(connectionSocket);
                thr.start();
            }
        } catch (IOException ex) {
            System.out.println("Cannot open socket on port " + args[0]);
        }
    }

    static class httpThread extends Thread {

        Socket acceptsocket;
        int host, port;
        String url;

        httpThread(Socket acceptsocket) {
            this.acceptsocket = acceptsocket;
        }

        @Override
        public void run() {
            try {
                Socket client = acceptsocket;
                BufferedInputStream clientIn = new BufferedInputStream(client.getInputStream());
                OutputStream clientOut = client.getOutputStream();

                StringBuilder outputStream = new StringBuilder();
                outputStream.append("\nLocal-Host: ");
                outputStream.append(client.getLocalAddress());
                outputStream.append("\nLocal-Port: ");
                outputStream.append(client.getLocalPort());
                outputStream.append("\nRemote-Host: ");
                outputStream.append(client.getInetAddress());
                outputStream.append("\nRemote-Port: ");
                outputStream.append(client.getPort());
                outputStream.append("\nData: ");

                outputStream.append(readStuffFromClient(clientIn));
                System.out.println(outputStream.toString());
                clientOut.write("HTTP/1.1 200 OK\r\n\r\nHttpMirror v1.1 - by zyclonite networx".getBytes());
                clientOut.write(outputStream.toString().getBytes());
                clientOut.flush();
                clientOut.close();
                clientIn.close();
                client.close();
            } catch (Exception e) {
                //System.out.println(e);
            }
        }

        private String readStuffFromClient(InputStream clientdata) {
            ByteArrayOutputStream response = new ByteArrayOutputStream();
            StringBuffer request = new StringBuffer(8192);
            int n;
            byte[] buffer = new byte[8192];

            try {
                if ((n = clientdata.read(buffer)) != -1) {
                    response.write(buffer, 0, n);
                    //response.flush();
                }
                request = new StringBuffer(response.toString());
            } catch (Exception e) {
                //System.out.println(e);
            }
            return request.toString();
        }
    }
}