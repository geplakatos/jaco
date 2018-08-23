package taviranyito;

import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.api.*;
import java.net.*;
import org.json.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.lang.reflect.*;
import java.awt.image.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.nio.*;

public class WebsocketClientHandler implements WebSocketListener, WriteCallback {

    private static final Logger logger = Logger.getLogger(WebsocketClientHandler.class);

    private static final int CLOSE_BASE_CODE = 4000;
    private static final int CLOSE_UNEXPECTED_RESULT = 1;

    private Session sess;

    public WebsocketClientHandler() {
    }

    public void writeFailed(Throwable x) {
        logger.error("writefailed", x);
    }

    public void sendRaw(String s) {
        try {
            logger.info("SendRaw: " + s);
            sess.getRemote().sendString(s, this);
        } catch (Throwable t) {
            logger.error("sendraw", t);
        }
    }

    public void close(int code) {
        try {
            logger.info("close" + code + " " + this);
            sess.close(CLOSE_BASE_CODE + code, "" + code);
        } catch (Throwable t) {
            logger.error("close error", t);
        }
    }

    public void writeSuccess() {
    }

    @Override
    public void onWebSocketConnect(Session _sess) {
        try {
            logger.info(_sess);
            URI u = _sess.getUpgradeRequest().getRequestURI();

            sess = _sess;

            for (String subprotocol : sess.getUpgradeRequest().getSubProtocols()) {
                if (subprotocol != null) {
                    if (subprotocol.replace("taviranyito__", "").equals(Taviranyito.secret)) {
                        logger.info("connect" + subprotocol + " " + u);
                        return;
                    }
                }
            }
        } catch (Throwable t) {
            logger.error("connerror", t);
        }
        close(CLOSE_UNEXPECTED_RESULT);
    }

    @Override
    public void onWebSocketText(String message) {
        try {
            JSONObject obj = new JSONObject(message);
            String type = obj.getString("type");
            Robot bot = new Robot();
            switch (type) {
                case "mousescreenshot": {
                    int w = obj.getInt("w");
                    int h = obj.getInt("h");
                    PointerInfo a = MouseInfo.getPointerInfo();
                    Point b = a.getLocation();
                    int x = (int) b.getX();
                    int y = (int) b.getY();
                    BufferedImage bi = bot.createScreenCapture(new Rectangle(x - w / 2, y - h / 2, w, h));
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(bi, "jpg", baos);
                    baos.flush();
                    byte[] bb = baos.toByteArray();
                    sess.getRemote().sendBytes(ByteBuffer.wrap(bb));
                    baos.close();
                    break;
                }
                case "alttab":
                    bot.keyPress(KeyEvent.VK_ALT);
                    bot.delay(100);
                    bot.keyPress(KeyEvent.VK_TAB);
                    bot.delay(100);
                    bot.keyRelease(KeyEvent.VK_TAB);
                    bot.delay(100);
                    bot.keyRelease(KeyEvent.VK_TAB);
                    bot.delay(100);
                    bot.keyRelease(KeyEvent.VK_ALT);
                    break;
                case "leftclick":
                    bot.mousePress(InputEvent.BUTTON1_MASK);
                    bot.mouseRelease(InputEvent.BUTTON1_MASK);
                    break;
                case "middleclick":
                    bot.mousePress(InputEvent.BUTTON2_MASK);
                    bot.mouseRelease(InputEvent.BUTTON2_MASK);
                    break;
                case "rightclick":
                    bot.mousePress(InputEvent.BUTTON3_MASK);
                    bot.mouseRelease(InputEvent.BUTTON3_MASK);
                    break;
                case "wheelup":
                    bot.mouseWheel(-1);
                    break;
                case "wheeldown":
                    bot.mouseWheel(1);
                    break;
                case "move": {
                    PointerInfo a = MouseInfo.getPointerInfo();
                    Point b = a.getLocation();
                    int x = (int) b.getX();
                    int y = (int) b.getY();
                    int dx = obj.getInt("dx");
                    int dy = obj.getInt("dy");
                    bot.mouseMove(x + dx, y + dy);
                    break;
                }
                case "keyup":
                    int code = obj.getInt("code");
                    String constname = ("VK_" + (char) code).toUpperCase();
                    int keycode = -1;
                    try {
                        Field[] f = KeyEvent.class.getFields();
                        for (int i = 0; i < f.length; i++) {
                            if (f[i].getName().equals(constname)) {
                                keycode = f[i].getInt(null);
                            }
                        }
                    } catch (Exception e) {
                        logger.error("fieldreaderror", e);
                    }
                    logger.info("keyup" + " int: " + code + " char: " + (char) code + " " + " keycode: " + keycode);
                    if (keycode != -1) {
                        bot.keyPress(keycode);
                        bot.keyRelease(keycode);
                    }
                    break;
            }
            //logger.info(message);
        } catch (Throwable t) {
            logger.error("readtext:" + message, t);
        }
    }

    @Override
    public void onWebSocketBinary(byte[] payload, int offset, int len) {
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        try {
            logger.info("close" + statusCode + " " + reason);
        } catch (Throwable t) {
            logger.error("close" + statusCode + " " + reason, t);
        }
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        logger.error("socketerror", cause);
    }
}
