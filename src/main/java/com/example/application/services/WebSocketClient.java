package com.example.application.services;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;


@Service
@ClientEndpoint
public class WebSocketClient {
  
    private boolean authenticated = false;
    public Session session;
    
    @Value("${auth.api}")
    private String api;

    @Value("${auth.secret}")
    private String secret;

    @Value("${auth.passphrase}")
    private String pass;

    private final String privateUri = "wss://ws.bitget.com/v2/ws/private";

    @OnOpen
    public void onOpen(Session session) {

        this.session = session;
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String sign = generateSignature(timestamp, "GET", "/user/verify", secret);
        String loginString = generateLoginString(api, pass, timestamp, sign);
        session.getAsyncRemote().sendText(loginString);
    }

    @OnMessage
    public void onMessage(String message) {
        if (message.contains("login")) {
            authenticated = true;
        }
        System.out.println("Received: " + message);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
    }

    private String generateSignature(String timestamp, String method, String requestPath, String secretKey) {
        String message = timestamp + method + requestPath;

        try {
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256Hmac.init(secretKeySpec);

            byte[] hashByte = sha256Hmac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashByte);
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate hmac-sha256", e);
        }
    }

    private String generateLoginString(String api, String pass, String timestamp, String sign) {
        return "{'op':'login','args':[{'apiKey':'" + api + "','passphrase':'" + pass + "','timestamp':'" + timestamp + "','sign':'" + sign + "'}]}";
    }

    public void connect(String uri) {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        try {
            container.connectToServer(this, new URI(uri));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        WebSocketClient client = new WebSocketClient();
    client.connect(client.privateUri);
    // Keep the application running until the WebSocket is closed
    while (client.session.isOpen()) {
        // Sleep for a short period to avoid busy-waiting
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // If the sleep is interrupted, exit the loop
            break;
        }
    }
    }

    // public static void main(String[] args) {
    //     HttpClient httpClient = HttpClient.newConnection();
    //     httpClient.websocket()
    //             .uri("ws://localhost:8080/ws")
    //             .handle((inbound, outbound) -> {
    //                 Flux<String> receiveFlux = inbound.receive()
    //                         .asString();
    //                 receiveFlux.subscribe(data -> {
    //                     System.out.println("Received: " + data);
    //                 });
    //                 return outbound.sendString(Flux.just("Hello"));
    //             })
    //             .then()
    //             .block();
    // }

    // private String authenticate(){
    //     long timestamp = Instant.now().getEpochSecond();
    //     String message = timestamp + "GET" + "/user/verify";

    // try {
    //     Mac sha256Hmac = Mac.getInstance("HmacSHA256");
    //     SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    //     sha256Hmac.init(secretKey);

    //     byte[] hashByte = sha256Hmac.doFinal(message.getBytes(StandardCharsets.UTF_8));
    //     return Base64.getEncoder().encodeToString(hashByte);
    // } catch (Exception e) {
    //     throw new RuntimeException("Failed to calculate hmac-sha256", e);
    // }
    // }

}
