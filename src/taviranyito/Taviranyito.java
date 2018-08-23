package taviranyito;

import java.sql.*;
import java.util.*;
import java.net.*;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.servlet.*;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.websocket.servlet.*;
import org.eclipse.jetty.websocket.*;
import org.json.*;
import org.apache.log4j.*;
import java.io.File;
import java.net.*;

public class Taviranyito {

    private Server server;
    public static String secret = null;
    public static int port = 0;
    private static final Logger logger = Logger.getLogger(Taviranyito.class);

    public void printNetInterfacesInfo() throws Exception {
        for (final Enumeration< NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces(); interfaces.hasMoreElements();) {
            final NetworkInterface cur = interfaces.nextElement();

            if (cur.isLoopback()) {
                continue;
            }           

            int f = 0;
            for (final InterfaceAddress addr : cur.getInterfaceAddresses()) {
                final InetAddress inet_addr = addr.getAddress();

                if (!(inet_addr instanceof Inet4Address)) {
                    continue;
                }

                if (f == 0) {
                    f++;
                    logger.info("interface " + cur.getName());
                }
                
                logger.info(
                        "  address: " + inet_addr.getHostAddress()
                        + "/" + addr.getNetworkPrefixLength()
                );

                logger.info(
                        "  broadcast address: "
                        + addr.getBroadcast().getHostAddress()
                );
            }
        }    
    }    

    public Taviranyito() throws Throwable {
        server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        server.addConnector(connector);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        ServletHolder websocket = new ServletHolder("ws-events", WSServlet.class);
        context.addServlet(websocket, "/ws");

        ServletHolder apihandler = new ServletHolder("web-events", ApiServlet.class);
        context.addServlet(apihandler, "/*");
        
        printNetInterfacesInfo();

        server.start();
    }

    public static void main(String[] args) throws Throwable {
        PropertyConfigurator.configure(Taviranyito.class.getResourceAsStream("log4j.properties"));
        
        String os = System.getProperty("os.name");
        logger.info("OS: "+os);
        if (os.toLowerCase().indexOf("win") == -1) {
            logger.info("Only windows supported! ("+os+")");
            System.exit(-1);
        }
        
        if (args.length < 2) {
            logger.info("Usage: java -jar taviranyito.jar port secretkey");
            System.exit(-1);
        }
        
        if (args.length == 3 && args[2].equals("adminstarted")) {
            Taviranyito.port = Integer.parseInt(args[0]);
            Taviranyito.secret = args[1];
            new Taviranyito();
        } else {
            String p = new File(Taviranyito.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getAbsolutePath();
            if (p.indexOf(".jar") != -1) {
                String cmd = "powershell.exe Start-Process -FilePath java.exe -Argument '-jar "+p+" "+args[0]+" "+args[1]+" adminstarted' -verb RunAs";
                logger.info("Start process: "+cmd);
                Runtime.getRuntime().exec(cmd);                                
            }
        }
    }
}
