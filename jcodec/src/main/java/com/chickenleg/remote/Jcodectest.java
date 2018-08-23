package com.chickenleg.remote;

import java.nio.ByteBuffer;
import org.jcodec.codecs.h264.*;
import org.jcodec.common.model.*;
import org.jcodec.scale.*;
import java.io.*;
import java.awt.image.*;
import java.awt.*;
import javax.imageio.ImageIO;
import org.jcodec.codecs.h264.io.model.*;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class Jcodectest {

    public static void main(String[] a) throws Exception {
        int w = 200;
        int h = 200;
        H264Encoder enc = H264Encoder.createH264Encoder();
        ColorSpace[] cs = enc.getSupportedColorSpaces();
        for (ColorSpace c : cs) {
            System.out.println(c);;
            
        }
        //System.exit(8);
        

        ByteBuffer out = ByteBuffer.allocate(1000 * 1000);
        ByteBuffer ret = null;

        int[][] t = new int[3][w * h];//pic.getData();
        Robot r = new Robot();
        /*for (int i=0;i<t.length;i++) {
            for (int j=0;j<t[i].length;j++) {
                t[i][j] = i+j;
                System.out.println(t[i][j]);
            }
        }
        System.out.println("1");
        System.exit(0);*/
/*


for (int k = 0; k < 1000; k++) {
    java.awt.Point ppp = MouseInfo.getPointerInfo().getLocation();
    ppp.x += 1;
    ppp.y += 1;
    r.mouseMove(ppp.x,ppp.y);
    Thread.sleep(10);
    r.setAutoDelay(0);
    r.mouseWheel(1);
}
System.exit(8);
*/
        for (int k = 0; k < 10; k++) {
            FileOutputStream fos = new FileOutputStream("3333__" + k);
            BufferedImage src = r.createScreenCapture(new Rectangle(0,0,w,h));//new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            
            int[] data = ((DataBufferInt) src.getRaster().getDataBuffer()).getData();

            /*for (int j = 0; j < data.length; j++) {
                data[j] = (int) (Math.random() * 1000);
            }*/
            Picture8Bit pic = AWTUtil.fromBufferedImage8Bit(src, ColorSpace.YUV420J);

            out.clear();
            ret = enc.encodeFrame8Bit(pic, out);
            byte[] tt = ret.array();
            fos.write(tt, 0, ret.limit());
            fos.flush();
            fos.close();

            ImageIO.write(src, "jpg", new File("alma__" + k + ".jpg"));
            /*System.out.println(ret.position());
             */
            System.out.println(k + " lm" + h + " " + out.position() + " " + out + " " + ret + " " + t.length + " " + t[0].length);
            //break;
        }
        //System.out.println(ret.capacity()+" "+ret.limit());

        H264Decoder dec = null;
        for (int k = 0; k < 10; k++) {
            System.out.println("ffddfs"+k);
            RandomAccessFile aFile = new RandomAccessFile("3333__" + k, "r");
            FileChannel inChannel = aFile.getChannel();
            long fileSize = inChannel.size();
            ByteBuffer buffer = ByteBuffer.allocate((int) fileSize);
            inChannel.read(buffer);
            
            if (dec == null) {
                dec = H264Decoder.createH264DecoderFromCodecPrivate(buffer.array());
                System.out.println(dec);
            }
            
            buffer.rewind();
            //buffer.flip();
            /*for (int i=0;i<21;i++) {
                int tt = buffer.get();
                System.out.println(i+" "+tt);;
            }*/
            byte[][] bbb = new byte[3][300* 300];
            org.jcodec.codecs.h264.io.model.Frame f = dec.decodeFrame8Bit(buffer, bbb).cloneCropped();
            BufferedImage outt = AWTUtil.toBufferedImage8Bit(f);
            ImageIO.write(outt, "jpg", new File("out_alma__" + k + ".jpg"));
            
            
            /*for (int i=0;i<bbb.length;i++) {
                for (int j=0;j<bbb[i].length;j++) {
                    System.out.print(bbb[i][j]);
                }                
            }*/
            System.out.println("ffddfs"+k);
            //System.out.println();
        }
        System.out.println("ffddfsend");
        //http://stackoverflow.com/questions/21163498/decode-h264-from-array-of-integers
        //https://www.google.hu/webhp?sourceid=chrome-instant&ion=1&espv=2&ie=UTF-8#q=jcodec%20decode%20raw
        for (int i = 0; i < ret.limit(); i++) {
            //System.out.println(i + " " + ret.get(i) + " " + out.get(i));
        }
        //java -cp ../jcodec-javase-0.2.0.jar:../jcodec-0.2.0.jar:remote-1.0-SNAPSHOT.jar com.chickenleg.remote.Jcodectest
        //cd ../target;java -cp ../jcodec-javase-0.2.0.jar:../jcodec-0.2.0.jar:remote-1.0-SNAPSHOT.jar com.chickenleg.remote.Jcodectest

    }

}
