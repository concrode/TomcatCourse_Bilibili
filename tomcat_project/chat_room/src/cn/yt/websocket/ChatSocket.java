package cn.yt.websocket;

import com.alibaba.fastjson.JSON;
import utils.MessageUtil;

import javax.servlet.http.HttpSession;
import javax.websocket.EndpointConfig;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.DoubleToIntFunction;

@ServerEndpoint(value = "/websocket", configurator = GetHttpSessionConfigurator.class)
public class ChatSocket {

    // This websocket session
    private static Session session;

    // Http servlet session
    private static HttpSession httpSession;

    // Each client has a ChatSocket instance. HttpSession in Map is current user's httpSession
    private static Map<HttpSession, ChatSocket> onlineUsers = new HashMap<HttpSession, ChatSocket>();

    // Calculate number of online users
    private static int onlineCount = 0;

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        this.session = session;

        // This config is from "GetHttpSessionConfigurator.class" -> modifyHandshake(ServerEndpointConfig config, ...)
        // Get current user's HttpSession
        HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        this.httpSession = httpSession;

        System.out.println("Current login user: " + httpSession.getAttribute("username") + ", Endpoint: " + hashCode());


        // Store current user's HttpSession and its ChatSocket instance
        if (httpSession.getAttribute("username") != null) {
            onlineUsers.put(httpSession, this);
        }

        // Get all current login users ----> TOM, TONY, JACK, ....
        String names = getNames();

        // Assemble message ---> {"data":"TOM, TONY, JACK","toName":"","fromName":"","type":"user"}
        String message = utils.MessageUtil.getContent(MessageUtil.TYPE_USER, "", "", names);

        // Broadcast message
        //session.getBasicRemote().sendText("");
        broadcastAllUsers(message);

        // Number of login users
        incrCount();

    }

    /**
     * {"fromName" : "TOM", "toName" : "Jack", "content" : "How are you?"}
     *
     * @param message
     * @param session
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("onMessage : name = " + httpSession.getAttribute("username") + ", message = " + message);

        Map<String, String> messageMap = JSON.parseObject(message, Map.class);
        String fromName = messageMap.get("fromName");
        String toName = messageMap.get("toName");
        String content = messageMap.get("content");

        if (toName == null || toName.isEmpty()) {
            return;
        }

        String messageContent = MessageUtil.getContent(MessageUtil.TYPE_MESSAGE, fromName, toName, content);
        System.out.println("Server to Client, Message: " + messageContent);
        if ("all".equals(toName)) {
            broadcastAllUsers(messageContent);
        } else {
            singlePushMessage(messageContent, fromName, toName);
        }

    }

    private void singlePushMessage(String content, String fromName, String toName) {
        boolean isOnline = false;

        for (HttpSession httpSession : onlineUsers.keySet()) {
            if (toName.equals(httpSession.getAttribute("username"))) {
                isOnline = true;
            }
        }

        if (isOnline) {
            for (HttpSession httpSession : onlineUsers.keySet()) {
                // Present messages in both sender and receiver window
                if (httpSession.getAttribute("username").equals(fromName) ||
                        httpSession.getAttribute("username").equals(toName)) {
                    try {
                        onlineUsers.get(httpSession).session.getBasicRemote().sendText(content);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    // Broadcast messages
    private void broadcastAllUsers(String message) {
        for (HttpSession httpSession : onlineUsers.keySet()) {
            try {
                onlineUsers.get(httpSession).session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private String getNames() {
        String names = "";
        if (onlineUsers.size() > 0) {
            for (HttpSession httpSession : onlineUsers.keySet()) {
                String username = (String) httpSession.getAttribute("username");
                names += username + ",";
            }
        }

       return names.substring(0, names.length() - 1);
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
