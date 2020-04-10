package com.harry.controller;

import com.harry.auth.Authentication;
import com.harry.model.Greeting;
import com.harry.model.HelloMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

@RestController
public class GreetingController {

    private final SimpMessagingTemplate messagingTemplate;

    /*
     * 实例化Controller的时候，注入SimpMessagingTemplate
     */
    @Autowired
    public GreetingController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /*
     * 使用restful风格
     */
    @MessageMapping("/demo1/hello/{typeId}")
    @SendTo("/topic/demo1/greetings")
    public Greeting greeting(@DestinationVariable Integer typeId, HelloMessage message, @Headers Map<String, Object> headers) throws Exception {
        return new Greeting(headers.get("simpSessionId").toString(), typeId + "---" + message.getMessage());
    }

    /*
     * 这里没用@SendTo注解指明消息目标接收者，消息将默认通过@SendTo("/topic/twoWays")交给Broker进行处理
     * 不推荐不使用@SendTo注解指明目标接受者
     */
    @MessageMapping("/demo1/twoWays")
    public Greeting twoWays(HelloMessage message) {
        return new Greeting("这是没有指明目标接受者的消息:", message.getMessage());
    }

    @MessageMapping("/demo2/hello/{typeId}")
    @SendTo("/topic/demo2/greetings")
    public Greeting greeting(HelloMessage message, StompHeaderAccessor headerAccessor) throws Exception {

        Authentication user = (Authentication) headerAccessor.getUser();

        if (null==user){
            return new Greeting("账号","未登录. ");
        }

        String sessionId = headerAccessor.getSessionId();

        return new Greeting(user.getName(), "sessionId: " + sessionId + ", message: " + message.getMessage());
    }

    @MessageMapping("/demo3/hello/{destUsername}")
    public Greeting greeting(@DestinationVariable String destUsername, HelloMessage message, StompHeaderAccessor headerAccessor) throws Exception {

        Authentication user = (Authentication) headerAccessor.getUser();

        if (null==user){
            return new Greeting("账号","未登录. ");
        }
        String sessionId = headerAccessor.getSessionId();

        Greeting greeting = new Greeting(user.getName(), "sessionId: " + sessionId + ", message: " + message.getMessage());

        /*
         * 对目标进行发送信息
         */
        messagingTemplate.convertAndSendToUser(destUsername, "/demo3/greetings", greeting);

        return new Greeting("系统", new Date().toString() + "消息已被推送。");
    }
}
