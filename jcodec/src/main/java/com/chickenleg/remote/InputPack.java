package com.chickenleg.remote;

import java.io.*;
import java.lang.reflect.Method;
import java.net.*;
import java.nio.*;
import org.json.*;
import java.awt.image.*;

public class InputPack {

    private static final int INPUT_BINARY = 1;    
    private static final int INPUT_TEXT = 2;
    private static final int INPUT_FRAME = 3;

    private int type;
    private String methodname;
    private byte[] bdata;
    private JSONObject jdata;

    public int getType() {
        return type;
    }
    
    public boolean isBinary() {
        return type == INPUT_BINARY;
    }
    
    public boolean isText() {
        return type == INPUT_TEXT;
    }
    
    public boolean isFrame() {
        return type == INPUT_FRAME;
    }
    
    public BufferedImage getFrame() {
        return null;
    }
    
    public byte[] getBinaryData() {
        return bdata;
    }

    public JSONObject getDataAsJSON() throws Exception {
        synchronized (bdata) {
            if (jdata == null) {
                jdata = new JSONObject(new String(bdata, "UTF-8"));                
            }
        }
        return jdata;
    }
    
    public String getMethodName() {
        return methodname;
    }

    public static InputPack read(ClientConnection cc, DataInputStream dis) throws Exception {
        InputPack ret = null;
        if (dis.available() >= 12) {
            ret = new InputPack();
            ret.type = dis.readInt();
            byte[] method = new byte[dis.readInt()];
            ret.bdata = new byte[dis.readInt()];

            dis.readFully(method);
            dis.readFully(ret.bdata);

            ret.methodname = new String(method, "UTF-8");            
        }
        
        return ret;
    }
    
    public static void sendFrame(DataOutputStream dos,ByteBuffer bb) throws Exception {
        int len = bb.limit();
        bb.rewind();
        dos.writeInt(INPUT_FRAME);
        dos.writeInt(0);
        dos.writeInt(len);
        dos.write(bb.array(),0,len);
        dos.flush();        
    }
}
