package com.chickenleg.remote;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.image.*;
import java.nio.*;
import javax.swing.*;
import java.util.*;
import org.jcodec.codecs.h264.*;
import org.jcodec.scale.*;

public class LocalClient extends Thread {

    class ImageDrawer extends Component {

        private BufferedImage bi;

        public void paint(Graphics g) {
            g.setColor(Color.RED);
            int x = 0;//new Random().nextInt(100);
            int y = 0;//new Random().nextInt(100);
            g.fillRect(x, y, 100, 100);
            if (bi != null) {
                synchronized (this) {
                    g.drawImage(bi, x, y, null);
                }
            }
        }

        public void addImage(BufferedImage bi) {
            synchronized (this) {
                this.bi = bi;
            }
        }
    }

    private JFrame frame;
    private Socket socket;
    private ImageDrawer id;
    private DataInputStream dis;
    private H264Decoder decoder;

    public static void main(String[] args) throws Exception {
        new LocalClient();
    }

    public LocalClient() throws Exception {
        socket = new Socket();
        socket.connect(new InetSocketAddress(Server.PORT));
        dis = new DataInputStream(socket.getInputStream());

        frame = new JFrame("Test Client");
        frame.setSize(200, 200);
        frame.setBounds(0, 0, 200, 200);

        id = new ImageDrawer();
        id.setPreferredSize(new Dimension(200, 200));

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(id, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        new Thread(this).start();
    }

    public void run() {
        long n = 0, kn = 0;
        long start = System.currentTimeMillis();
        double speed = 0;
        byte[][] bbb = new byte[3][300 * 300];
        byte[] data = new byte[300 * 300 * 3];
        Runtime runtime = Runtime.getRuntime();
        while (true) {
            try {
                if (dis.available() > 12) {
                    int type = dis.readInt();
                    int mt = dis.readInt();
                    int len = dis.readInt();
                    n += 12 + len;
                    kn = n / 1024;
                    long diff = (System.currentTimeMillis() - start) / 1000;
                    Log.log(type, mt, len);

                    if (diff > 10) {
                        diff = 0;
                        speed = 0;
                        start = System.currentTimeMillis();
                        n = 0;
                    }

                    if (diff > 0) {
                        speed = kn / diff;
                    }
                    dis.readFully(data, 0, len);
                    ByteBuffer bb = ByteBuffer.wrap(data);
                    bb.limit(len);
                    bb.position(0);
                    Log.log(len, speed + " KB/sec", diff + " sec", kn + " KB",runtime.totalMemory());

                    if (decoder == null) {
                        decoder = H264Decoder.createH264DecoderFromCodecPrivate(bb.array());
                    }
                    org.jcodec.codecs.h264.io.model.Frame f = decoder.decodeFrame8Bit(bb, bbb).cloneCropped();
                    id.addImage(AWTUtil.toBufferedImage8Bit(f));
                    id.repaint();
                } else {
                    Thread.sleep(100);
                }
            } catch (Exception e) {
                try {
                    Thread.sleep(100);
                } catch (Exception ex) {
                }
                Log.log(e);
            }
        }
    }

}
