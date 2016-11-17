/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.networking.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;
import java.sql.*;

/**
 *
 * @author gemal
 */
@ApplicationScoped
public class UserManager {
    
        private int deviceId = 0;
    private final Set<Session> sessions = new HashSet<>();
    private final Set<User> devices = new HashSet<>();
    
        public void addSession(Session session) {
        sessions.add(session);
        /*for (User device : devices) {
            JsonObject addMessage = loginMessage(device.id);
            sendToSession(session, addMessage);
        }*/

    }
        
    public void broadcastLogin(String name) {
        JsonProvider provider = JsonProvider.provider();
        System.out.println("Sending login message to all clients!");
            JsonObject updateDevMessage = provider.createObjectBuilder()
                    .add("action", "loggedIn")
                    .add("user", name)
                    .build();
            sendToAllConnectedSessions(updateDevMessage);
    }
    
    public void broadcastUserList() {
        String names = "";
        for(User user : devices)
            names += user.name+" ";
        JsonProvider provider = JsonProvider.provider();
            JsonObject updateDevMessage = provider.createObjectBuilder()
                    .add("action", "userList")
                    .add("size", devices.size())
                     .add("names", names)
                    .build();
            System.out.println("I AM VERY HAPPY TO SPREAD USER LIST");
            sendToAllConnectedSessions(updateDevMessage);
    }
    
    public void broadcastLogout(String name) {
        JsonProvider provider = JsonProvider.provider();
        System.out.println("Sending message to all clients!");
        System.out.println("Sending 1 login message!");
            JsonObject updateDevMessage = provider.createObjectBuilder()
                    .add("action", "loggedOut")
                    .add("user", name)
                    .build();
            sendToAllConnectedSessions(updateDevMessage);
    }

    public void broadcastMessage(String message, String name) {
        JsonProvider provider = JsonProvider.provider();
        System.out.println("Sending message to all clients!");
        System.out.println("Sending 1 login message!");
                    String msg = name+": "+message;
            JsonObject updateDevMessage = provider.createObjectBuilder()
                    .add("action", "message")
                    .add("message", msg)
                    .build();
            sendToAllConnectedSessions(updateDevMessage);
            insertDatabase(name, message);
    }
        
    public void loginMessage(int id) {
        //JsonProvider provider = JsonProvider.provider();
        User user = getUserById(id);
        if (user != null) {
        broadcastLogin(user.name);
        }
    }
    private User getUserById(int ID) {
        for (User user : devices) {
            if (user.id == ID) {
                return user;
            }
        }
        return null;
    }
        
    public void removeDevice(int id) {
        User device = getUserById(id);
        if (device != null) {
            devices.remove(device);
            JsonProvider provider = JsonProvider.provider();
            JsonObject removeMessage = provider.createObjectBuilder()
                    .add("action", "remove")
                    .add("id", id)
                    .build();
            sendToAllConnectedSessions(removeMessage);
        }
    }
        
            private void sendToSession(Session session, JsonObject message) {
        try {
            session.getBasicRemote().sendText(message.toString());
        } catch (IOException ex) {
            sessions.remove(session);
            Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
            
               private void sendToAllConnectedSessions(JsonObject message) {
        for (Session session : sessions) {
            sendToSession(session, message);
        }
    }

    public void addDevice(User device) {
        device.setId(deviceId);
        devices.add(device);
        loginMessage(deviceId);
        broadcastUserList();
        deviceId++;
        //JsonObject addMessage = createAddMessage(device);
        //sendToAllConnectedSessions(addMessage);
    }
    
    public void removeSession(Session session) {
        User removed = getUserBySession(session);
        sessions.remove(session);
        if(removed != null) {
            devices.remove(removed);
            broadcastLogout(removed.name);
            broadcastUserList();
        }
    }
    
    public User getUserBySession(Session session) {
        for(User user : devices)
            if(user.sessionId.equals(session.getId()))
                return user;
        return null;
    }
    
    public void userHistory(Session session) {
        String userName = getUserBySession(session).name;
        String[] messages = retrieveAllMessages(userName);
        int first = 1;
        for(String m : messages) {
            
                        JsonProvider provider = JsonProvider.provider();
            JsonObject msg = provider.createObjectBuilder()
                    .add("action", "history")
                    .add("message", m)
                    .add("first", first)
                    .build();
            
            sendToSession(session, msg);
            first = 0;
        }
    }
    
    	public static void insertDatabase(String name, String message) {
      Connection conn = null;
      try {
         Class.forName("org.postgresql.Driver");
         conn = DriverManager
            .getConnection("jdbc:postgresql://localhost:5432/mydb",
            "postgres", "gemal");
      System.out.println("Opened database successfully");
     //STEP 4: Execute a query
      System.out.println("Creating statement...");
      Statement stmt = conn.createStatement();
      String sql;
      sql = "insert into messages values ('"+name+"', '"+message+"');";
      stmt.executeUpdate(sql);
conn.close();
      } catch (Exception e) {
         e.printStackTrace();
         System.err.println(e.getClass().getName()+": "+e.getMessage());
      }
	}


	public static String[] retrieveAllMessages(String user) {
	ArrayList messages = new ArrayList();
      Connection conn = null;
      try {
         Class.forName("org.postgresql.Driver");
         conn = DriverManager
            .getConnection("jdbc:postgresql://localhost:5432/mydb",
            "postgres", "gemal");
      System.out.println("Opened database successfully");
     //STEP 4: Execute a query
      System.out.println("Creating statement...");
      Statement stmt = conn.createStatement();
      String sql;
      sql = "SELECT name, message FROM messages where name = '"+user+"';";
      ResultSet rs = stmt.executeQuery(sql);

      //STEP 5: Extract data from result set
      while(rs.next()){
         //Retrieve by column name
         String name = rs.getString("name");
         String message = rs.getString("message");

         //Display values
         messages.add(name+": " + message);
	
}
conn.close();
      } catch (Exception e) {
         e.printStackTrace();
         System.err.println(e.getClass().getName()+": "+e.getMessage());
         return null;
      }
return (String[]) messages.toArray(new String[messages.size()]);
	}
    
}
