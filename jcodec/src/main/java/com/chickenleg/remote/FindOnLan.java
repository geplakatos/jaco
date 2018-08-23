package com.chickenleg.remote;

import java.net.*;
import java.util.*;

public class FindOnLan extends Thread {

    private int port;
    private Vector<InetAddress> ret;
    private boolean end;

    public static class Server extends Thread {

        private int port;
        private DatagramSocket socket;

        public Server(int tcpport, int suggestport) throws Exception {
            socket = new DatagramSocket(suggestport);
            socket.setBroadcast(true);

            this.port = port;
            setDaemon(false);
            start();
        }

        public void run() {
            byte[] recvBuf = new byte[15000];
            while (true) {
                try {
                    Log.log("rec");
                    //Receive a packet
                    DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                    socket.receive(packet);
                    //Packet received
                    System.out.println(getClass().getName() + ">>>Discovery packet received from: " + packet.getAddress().getHostAddress());
                    System.out.println(getClass().getName() + ">>>Packet received; data: " + new String(packet.getData()));
                    //See if the packet holds the right command (message)
                    String message = new String(packet.getData()).trim();
                    if (message.equals("DISCOVER_FUIFSERVER_REQUEST")) {
                        byte[] sendData = ("DISCOVER_FUIFSERVER_RESPONSE"+port).getBytes();
                        //Send a response
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                        socket.send(sendPacket);
                        System.out.println(getClass().getName() + ">>>Sent packet to: " + sendPacket.getAddress().getHostAddress());
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                }
            }
        }

        public int getPort() {
            return port;
        }
    }

    public static class Client extends Thread {

        public Client(int suggestport) throws Exception {
            // Find the server using UDP broadcast
            //Open a random port to send the package
            DatagramSocket c = new DatagramSocket();
            c.setBroadcast(true);

            byte[] sendData = "DISCOVER_FUIFSERVER_REQUEST".getBytes();

            //Try the 255.255.255.255 first
            try {
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), suggestport);
                c.send(sendPacket);
                System.out.println(getClass().getName() + ">>> Request packet sent to: 255.255.255.255 (DEFAULT)");
            } catch (Exception e) {
            }

            // Broadcast the message over all the network interfaces
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();

                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    Log.log(networkInterface,networkInterface.isLoopback(), !networkInterface.isUp());
                 //   continue; // Don't want to broadcast to the loopback interface
                }

                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();                    
                    if (broadcast == null) {
                        continue;
                    }
                    
                    Log.log(broadcast instanceof Inet4Address,broadcast,broadcast instanceof Inet6Address);

                    // Send the broadcast package!
                    try {
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, suggestport);
                        c.send(sendPacket);
                    } catch (Exception e) {
                    }

                    System.out.println(getClass().getName() + ">>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
                }
            }

            System.out.println(getClass().getName() + ">>> Done looping over all network interfaces. Now waiting for a reply!");

            //Wait for a response
            byte[] recvBuf = new byte[15000];
            DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
            c.receive(receivePacket);

            //We have a response
            System.out.println(getClass().getName() + ">>> Broadcast response from server: " + receivePacket.getAddress().getHostAddress());

            //Check if the message is correct
            String message = new String(receivePacket.getData()).trim();
            if (message.indexOf("DISCOVER_FUIFSERVER_RESPONSE") != -1) {
                //DO SOMETHING WITH THE SERVER'S IP (for example, store it in your controller)
                Log.log(receivePacket.getAddress(),message);
            }

            //Close the port!
            c.close();

        }
    }

}
