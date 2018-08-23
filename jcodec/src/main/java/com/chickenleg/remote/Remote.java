package com.chickenleg.remote;


import java.awt.*;
import java.io.*;
import java.awt.image.*;
import javax.imageio.*;

public class Remote {
    
public static BufferedImage getDifferenceImage(BufferedImage img1, BufferedImage img2) {
    int width1 = img1.getWidth(); // Change - getWidth() and getHeight() for BufferedImage
    int width2 = img2.getWidth(); // take no arguments
    int height1 = img1.getHeight();
    int height2 = img2.getHeight();
    if ((width1 != width2) || (height1 != height2)) {
        System.err.println("Error: Images dimensions mismatch");
        System.exit(1);
    }

    // NEW - Create output Buffered image of type RGB
    BufferedImage outImg = new BufferedImage(width1, height1, BufferedImage.TYPE_INT_RGB);

    // Modified - Changed to int as pixels are ints
    int diff;
    int result; // Stores output pixel
    for (int i = 0; i < height1; i++) {
        for (int j = 0; j < width1; j++) {
            int rgb1 = img1.getRGB(j, i);
            int rgb2 = img2.getRGB(j, i);
            int r1 = (rgb1 >> 16) & 0xff;
            int g1 = (rgb1 >> 8) & 0xff;
            int b1 = (rgb1) & 0xff;
            int r2 = (rgb2 >> 16) & 0xff;
            int g2 = (rgb2 >> 8) & 0xff;
            int b2 = (rgb2) & 0xff;
            diff = Math.abs(r1 - r2); // Change
            diff += Math.abs(g1 - g2);
            diff += Math.abs(b1 - b2);
            diff /= 3; // Change - Ensure result is between 0 - 255
            // Make the difference image gray scale
            // The RGB components are all the same
            result = (diff << 16) | (diff << 8) | diff;
            outImg.setRGB(j, i, result); // Set result
        }
    }

    // Now return
    return outImg;
}    

    public static void main(String[] a) throws Exception {
        Robot r = new Robot();
        Point p = MouseInfo.getPointerInfo().getLocation();
        BufferedImage prev = r.createScreenCapture(new Rectangle(p.x - 100, p.y - 100, 200, 200));
        ImageIO.write(prev, "gif", new File("saved-1.gif"));            
        for (int i = 0; i < 20; i++) {
            p = MouseInfo.getPointerInfo().getLocation();
            BufferedImage image = r.createScreenCapture(new Rectangle(p.x - 100, p.y - 100, 200, 200));
            BufferedImage diff = getDifferenceImage(image,prev);
            File outputfile = new File("saved"+i+".gif");
            ImageIO.write(diff, "gif", outputfile);            
            Thread.sleep(100);
            System.out.println(i);
            
        }
/*        AWTSequenceEncoder enc = AWTSequenceEncoder.createSequenceEncoder(new File("filename.mp4"));        
        
        Robot r = new Robot();
enc.getEncoder().setKeyInterval(25);
        for (int i = 0; i < 200; i++) {
            Point p = MouseInfo.getPointerInfo().getLocation();
            BufferedImage image = r.createScreenCapture(new Rectangle(p.x - 100, p.y - 100, 200, 200));
            enc.encodeImage(image);
            Thread.sleep(100);
            System.out.println(i);
        }
        enc.finish();
*/
System.out.println("fdfsd");
    }

}
