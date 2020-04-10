package com.harry.websocket;

import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 聊天室
 */
@ServerEndpoint("/chat-room/{username}")
@Component
public class ChatRoomServerEndpoint {

    private static Map<String, Session> livingSessions = new ConcurrentHashMap<String, Session>();

    @OnOpen
    public void openSession(@PathParam("username") String username, Session session) {

        String sessionId = session.getId();

        livingSessions.put(sessionId, session);

        sendTextAll("欢迎用户[" + username + "] 来到聊天室！");

    }

    @OnMessage
    public void onMessage(@PathParam("username") String username, Session session, String message) {

        sendTextAll("用户[" + username + "] : " + message);
    }

    @OnClose
    public void onClose(@PathParam("username") String username, Session session) {

        String sessionId = session.getId();

        //当前的Session 移除
        livingSessions.remove(sessionId);

        //并且通知其他人当前用户已经离开聊天室了
        sendTextAll("用户[" + username + "] 已经离开聊天室了！");
    }

    private void sendTextAll(String message) {

        livingSessions.forEach((sessionId, session) -> {
            sendText(session,message);
        });
    }

    private void sendText(Session session, String message) {

        RemoteEndpoint.Basic basic = session.getBasicRemote();

        try {
            basic.sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
