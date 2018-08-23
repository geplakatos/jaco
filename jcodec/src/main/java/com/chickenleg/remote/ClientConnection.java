package com.chickenleg.remote;

import java.io.*;
import java.net.*;
import org.json.*;
import java.awt.*;
import java.awt.image.*;
import java.lang.reflect.*;
import java.nio.*;
import org.jcodec.codecs.h264.*;
import org.jcodec.common.model.*;
import org.jcodec.scale.*;

public class ClientConnection extends Thread {

    private static final int READ_TIMEOUT = 4000;
    private static final int MAX_FRAME_SIZE = 1000 * 1000;

    private DataInputStream dis;
    private DataOutputStream dos;
    private Socket s;
    private H264Encoder encoder;
    private ByteBuffer out;
    private FPSData fps;
    private BufferedImage src;
    private int[] isrc;
    private Picture8Bit pic;
    private int framewidth;
    private int frameheight;
    private Runtime runtime;
    private MemoryEconomiseScreenTaker taker;

    public ClientConnection(Socket s) throws Exception {
        Log.log("new connection");
        this.taker = new MemoryEconomiseScreenTaker();
        this.runtime = Runtime.getRuntime();
        this.pic = null;
        this.framewidth = 200;
        this.frameheight = 200;
        this.src = new BufferedImage(framewidth, frameheight, BufferedImage.TYPE_INT_RGB);
        this.isrc = ((DataBufferInt) src.getRaster().getDataBuffer()).getBankData()[0];
        this.encoder = H264Encoder.createH264Encoder();
        this.out = ByteBuffer.allocate(MAX_FRAME_SIZE);
        this.s = s;
        this.dis = new DataInputStream(s.getInputStream());
        this.dos = new DataOutputStream(s.getOutputStream());
        this.fps = new FPSData(FPSData.MIN_FPS);
        s.setSoTimeout(READ_TIMEOUT);
        setDaemon(false);
        start();
    }

    private void changeFPS(int fps) {
        this.fps.changeFPS(fps);
    }

    public void run() {
        while (!interrupted()) {
            try {
                processInput();
                sendFrame();
                Thread.sleep(10);
            } catch (Throwable t) {
                t.printStackTrace();
                interrupt();
            }
        }
        close();
    }

    private void close() {
        try {
            dis.close();
        } catch (Exception e) {
        }
        try {
            dos.close();
        } catch (Exception e) {
        }
        try {
            s.close();
        } catch (Exception e) {
        }
    }

    private void sendFrame() throws Exception {
        if (fps.isFrame()) {
            java.awt.Point p = MouseInfo.getPointerInfo().getLocation();
            Rectangle rect = new Rectangle(p.x - framewidth / 2, p.y - frameheight / 2, framewidth, frameheight);
            try {
                taker.take(rect.x, rect.y, rect.width, rect.height, isrc);
                pic = AWTUtil.fromBufferedImage8Bit(src, ColorSpace.YUV420J);
            } catch (Exception e) {
                Log.log(e.getMessage(), rect);
            }

            out.clear();
            out = encoder.encodeFrame8Bit(pic, out);
            InputPack.sendFrame(dos, out);
            Log.log("Send frame " + fps.getFrameCount(), out.limit(), runtime.totalMemory());
        }
    }

    private void processInput() throws Exception {
        InputPack ip = InputPack.read(this, dis);
        if (ip != null) {
            //todo process data
        }
    }
}
