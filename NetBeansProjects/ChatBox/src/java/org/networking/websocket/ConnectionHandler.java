/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.networking.websocket;

import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 *
 * @author gemal
 */
    
    @ApplicationScoped
    @ServerEndpoint("/actions")
    public class ConnectionHandler {
            @Inject
    private UserManager sessionHandler;

    @OnOpen
        public void open(Session session) {
            System.out.println("WOW THIS HAS WORKED THE WHOLE TIME LOL!");
            //String name = session.getAttribute("username");
            sessionHandler.addSession(session);
    }
        

    @OnClose
    public void close(Session session) {
        System.out.println("Connection lost");
        sessionHandler.removeSession(session);
    }

    @OnError
    public void onError(Throwable error) {
        System.out.println("Error detected!");
        Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, error);
    }

    @OnMessage
    public void handleMessage(String message, Session session) {
            System.out.println("Handling a message ...");
        try (JsonReader reader = Json.createReader(new StringReader(message))) {
            JsonObject jsonMessage = reader.readObject();
                System.out.println("message action: "+jsonMessage.getString("action"));
            if ("login".equals(jsonMessage.getString("action"))) {
                sessionHandler.addSession(session);
            }
            
            if ("history".equals(jsonMessage.getString("action"))) {
                sessionHandler.userHistory(session);
            }

            if ("message".equals(jsonMessage.getString("action"))) {
                /*device.setDescription(jsonMessage.getString("description"));
                device.setType(jsonMessage.getString("type"));
                device.setStatus("Off");*/
                String msg = jsonMessage.getString("message");
                String name = jsonMessage.getString("name");
                sessionHandler.broadcastMessage(msg, name);
            }
            
            if ("enter".equals(jsonMessage.getString("action"))) {
                sessionHandler.addSession(session);
                User device = new User();
                device.sessionId = session.getId();
                device.setName(jsonMessage.getString("name"));
                /*device.setDescription(jsonMessage.getString("description"));
                device.setType(jsonMessage.getString("type"));
                device.setStatus("Off");*/
                System.out.println("I am retarded and thats okay");
                sessionHandler.addDevice(device);
                
            }

            if ("remove".equals(jsonMessage.getString("action"))) {
                int id = (int) jsonMessage.getInt("id");
                sessionHandler.removeDevice(id);
            }

        }
    }
}
