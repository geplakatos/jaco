package taviranyito;

import org.eclipse.jetty.websocket.servlet.*;

public class WebsocketCreator implements WebSocketCreator {

    public WebsocketCreator() {
    }

    @Override
    public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp) {
        for (String subprotocol : req.getSubProtocols()) {
            if (subprotocol != null) {
                resp.setAcceptedSubProtocol(subprotocol);
                break;
            }
        }
        return new WebsocketClientHandler();
    }
}
