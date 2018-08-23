package taviranyito;

import org.eclipse.jetty.websocket.servlet.*;
import org.eclipse.jetty.websocket.servlet.*;


@SuppressWarnings("serial")
public class WSServlet extends WebSocketServlet {
    private static final int IDLETIMEOUT = 60000;
    
    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.getPolicy().setIdleTimeout(IDLETIMEOUT);
        factory.setCreator(new WebsocketCreator());
    }
}
