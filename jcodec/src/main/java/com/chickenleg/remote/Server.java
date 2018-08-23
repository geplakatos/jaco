package com.chickenleg.remote;

import java.awt.image.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.net.*;
import javax.imageio.ImageIO;

public class Server {

    public static final int PORT = 27492;

    public static void main(String[] args) throws Exception {
        new Server();
    }

    public static void testTaker(int w, int h, int num) throws Exception {
        Thread.sleep(2000);
        System.gc();
        System.out.println("\n\nTest Taker");
        //System.load("/home/geplakatos/work/util/java/remote/remote/lib/jni/libtake.so");
        //int[] container = new int[w * h];
        MemoryEconomiseScreenTaker m = new MemoryEconomiseScreenTaker();
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        int[] bb = ((DataBufferInt) bi.getRaster().getDataBuffer()).getBankData()[0];

        long memstart = Runtime.getRuntime().freeMemory();
        long start = System.currentTimeMillis();
        for (int i = 0; i < num; i++) {
            try {
                m.take(0, 0, w, h, bb);
            } catch (Exception e) {
                Log.log(i, e.getMessage());
            }
            //System.arraycopy(container, 0, bb, 0, container.length);
            //ImageIO.write(bi, "png", new File("test_take_" + i + ".png"));
        }
        long diff = (System.currentTimeMillis() - start) / 1000;
        long mem = (memstart - Runtime.getRuntime().freeMemory()) / 1024 / 1024;
        System.out.println("mem: " + mem + " MB");
        System.out.println("time: " + diff + " sec\nend test.");
    }

    public static void testRobot(int w, int h, int num) throws Exception {
        Thread.sleep(2000);
        System.gc();
        System.out.println("\n\nTest Robot");
        Robot robot = new Robot();
        Rectangle r = new Rectangle(0, 0, w, h);
        BufferedImage bi = null;

        long memstart = Runtime.getRuntime().freeMemory();
        long start = System.currentTimeMillis();
        for (int i = 0; i < num; i++) {
            bi = robot.createScreenCapture(r);
            //ImageIO.write(bi, "png", new File("test_robot_"+i+".png"));
        }
        long diff = (System.currentTimeMillis() - start) / 1000;
        long mem = (memstart - Runtime.getRuntime().freeMemory()) / 1024 / 1024;
        System.out.println("mem: " + mem + " MB");
        System.out.println("time: " + diff + " sec\nend test.");
    }

    public Server() throws Exception {        
        new JNIHelper().load("jni/libtake");        
        /*new FindOnLan.Server(40,8888);
        Thread.sleep(2000);
        new FindOnLan.Client(8888);
        System.exit(9);*/
        /*InetAddress[] ia = new FindOnLan(8000).getAvailableAddresses();
        for (InetAddress i : ia) {
            System.out.println(i);;
        }
        System.exit(7);*/
        /*int w = 500;
        int h = 500;
        int framen = 2000;

        testTaker(w, h, framen);
        testRobot(w, h, framen);*/
        System.exit(0);

        ServerSocket s = new ServerSocket(PORT);
        Log.log("waiting...");
        while (true) {
            new ClientConnection(s.accept());
        }
    }
}
