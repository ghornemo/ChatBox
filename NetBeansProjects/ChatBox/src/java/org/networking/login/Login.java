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
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;
import org.networking.websocket.ConnectionHandler;

/**
 *
 * @author gemal
 */
@ApplicationScoped
public class Login {
        private void sendToSession(Session session, JsonObject message) {
        try {
            session.getBasicRemote().sendText(message.toString());
        } catch (IOException ex) {
            Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
        public void loginResponse(int status, Session session) {
        JsonProvider provider = JsonProvider.provider();
            JsonObject updateDevMessage = provider.createObjectBuilder()
                    .add("action", "loginResponse")
                    .add("status", status)
                    .build();
            sendToSession(session, updateDevMessage);
    }
        
    public void registrationResponse(boolean status, Session session) {
        JsonProvider provider = JsonProvider.provider();
        System.out.println("Sending login message to all clients!");
            JsonObject updateDevMessage = provider.createObjectBuilder()
                    .add("action", "registrationResponse")
                    .add("status", status)
                    .build();
            sendToSession(session, updateDevMessage);
    }
        
    public boolean register(String name, String pass) throws IOException {
        File file = new File("users.txt");
        FileWriter fw;
        if (!file.exists()) {
            file.createNewFile();
        }
            try {
                fw = new FileWriter(file, true);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(name+" "+pass);
                bw.newLine();
                bw.close();
            } catch (IOException ex) {
                System.out.println("failed to register user! ");
                Logger.getLogger(LoginHandler.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        return true; // Successful registration!
    }
    
    public int loginRequest(String name, String pass, Session session) {
        try {
        Scanner s = new Scanner(new File("users.txt"));
        while(s.hasNext()) {
            String[] line = s.nextLine().split(" ");
            System.out.println("Name from text: "+line[0]);
            if(line[0].equalsIgnoreCase(name)) {
                if(line[1].equals(pass)) {
                    //session.close();
                    return 3; // Success
                }else
                return 2; // Invalid password
            }
        }
        try {//Attempt to register user...
            if(register(name, pass))
                return 4;//Successfully registered!
            else
                return 5; // Unsuccessful registration
        }catch(Exception e) {
        System.out.println("Server made an error, sad day :(");
        System.out.println(e);}
        return 1; // Invalid username
    }catch(Exception e) {}
        return 1;
    }
}
