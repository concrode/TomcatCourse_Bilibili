package cn.yt.websocket;

import javax.servlet.http.HttpSession;
import javax.websocket.EndpointConfig;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.Map;

@ServerEndpoint(value = "/websocket")
public class ChatSocket {

    // This websocket session
    private static Session session;

    // Http servlet session
    private static HttpSession httpSession;

    // Each client has a ChatSocket instance
    private static Map<HttpSession, ChatSocket> onlineUsers = new HashMap<HttpSession, ChatSocket>();

    private static int onlineCount = 0;

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        this.session = session;
    }

    public int getOnlineCount() {
        return onlineCount;
    }


    public synchronized void incrCount() {
        onlineCount ++;
    }

    public synchronized void decrCount() {
        onlineCount --;
    }

}
