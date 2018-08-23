package taviranyito;

import org.eclipse.jetty.websocket.servlet.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.eclipse.jetty.server.*;
import org.json.*;

@SuppressWarnings("serial")
public class ApiServlet extends HttpServlet {

    private static String indexhtml = null;
    private static Object lock = new Object();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {        
        synchronized (lock) {
            if (indexhtml == null) {
                InputStream is = getClass().getResourceAsStream("index.html");
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String ss = "";
                String s = "";
                while (null != (s = br.readLine())) {
                    ss += s + "\n";
                }
                indexhtml = ss.trim();
            }        
        }
        
        response.setContentType("text/html; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);        
        response.getOutputStream().print(indexhtml);
        
        //indexhtml = null;
    }
}
