/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.networking.login;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.spi.JsonProvider;
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
    @ServerEndpoint("/login")
    public class LoginHandler {
            @Inject
    private Login loginManager;

    /*@OnOpen
        public void open(Session session) {
            //ystem.out.println("Connection accepted!");
            //sessionHandler.addSession(session);
    }

    @OnClose
    public void close(Session session) {
        //System.out.println("Connection lost");
        //sessionHandler.removeSession(session);
    }

    @OnError
    public void onError(Throwable error) {
        //System.out.println("Error detected!");
        //Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, error);
    }*/

    @OnMessage
    public void handleMessage(String message, Session session) throws IOException {
        try (JsonReader reader = Json.createReader(new StringReader(message))) {
            JsonObject jsonMessage = reader.readObject();
                System.out.println("message action: "+jsonMessage.getString("action"));
                
            if ("login".equals(jsonMessage.getString("action"))) { // Login validation request
                System.out.println("Initiating login process...");
                String username = jsonMessage.getString("username");
                String password = jsonMessage.getString("password");
                System.out.println("Calculating login status...");
                int status = loginManager.loginRequest(username, password, session);
                System.out.println("Sending login respoinse...");
                loginManager.loginResponse(status, session);
                System.out.println("Login request sent.");
                if(status == 3) { //Close connection if successful.
                    session.getUserProperties().put("username", username);
                    //session.close();
                }
            }
            
            if ("register".equals(jsonMessage.getString("action"))) { // Registration request
                String username = jsonMessage.getString("username");
                String password = jsonMessage.getString("password");
                boolean status = loginManager.register(username, password);
                loginManager.registrationResponse(status, session);
            }

        }
    }

    
}
