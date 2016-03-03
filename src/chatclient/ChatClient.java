/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient;

/**
 *
 * @author Diego
 */

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;
import java.io.*;
import java.util.concurrent.locks.*;
import java.util.concurrent.*;

public class ChatClient extends JFrame {
    
    JTextArea jtaChat = new JTextArea();
    JLabel jlbName = new JLabel("Name");
    JLabel jlbMessage = new JLabel("Message");
    JTextArea jtaName = new JTextArea();
    JTextArea jtaMessage = new JTextArea();
    
    
    
    public ChatClient(){
        
        
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout( 2, 2, 5, 5) );
        jtaName.setColumns( 20);
        jtaName.setRows(1);
        jtaMessage.setColumns( 20);
        jtaMessage.setRows(1);
        
        topPanel.add(jlbName);
        topPanel.add(jtaName);
        topPanel.add(jlbMessage);
        topPanel.add(jtaMessage);
        
        setLayout(new BorderLayout() );
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(jtaChat), BorderLayout.CENTER);
        
        setTitle("Chat Client");
        setSize(500 , 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        
        
        try{
            Socket socket = new Socket("127.0.0.1", 8008);
            OutputHandler outputToServer = new OutputHandler(socket);
            InputHandler inputFromServer = new InputHandler(socket);
            new Thread(inputFromServer).start();
            
            jtaMessage.addKeyListener( new KeyAdapter(){
                @Override
                public void keyTyped(KeyEvent e){
                    char typedChar = e.getKeyChar();
                    if(typedChar == '\n'){
                        outputToServer.sendMessage(jtaName.getText() + ": " + jtaMessage.getText() );
                        jtaMessage.setText("");
                    }
                }
            });
            
            
            
        }catch(IOException ioEx){
            
        }
        
    }
    
    private class InputHandler implements Runnable{
        Socket serverSocket;
        
        InputHandler(Socket serverSocket){
            this.serverSocket = serverSocket;
        }
        
        
        @Override
        public void run(){
            while(jtaChat != null){
                try{
                    ObjectInputStream fromServer = new ObjectInputStream( serverSocket.getInputStream() );
                    String message = (String)(fromServer.readObject());
                    synchronized(jtaChat){
                        jtaChat.append(message);
                        jtaChat.repaint();
                    }
                    
                }catch(ClassNotFoundException clEx){
                    
                }catch(IOException ioEx){
                    
                }
            }
            try{
                serverSocket.close();
            }catch(Exception ex){
                
            }
            
        }
    }
    
    private class OutputHandler{
        Socket serverSocket;
        
        public OutputHandler(Socket serverSocket){
            this.serverSocket = serverSocket;
        }
        
        public boolean sendMessage(String message){
            try{
                ObjectOutputStream toServer = new ObjectOutputStream( serverSocket.getOutputStream() );
                
                toServer.writeObject(message);
                
                return true;
            }catch(IOException ioEx){
                return false;
            }
            
        }
    }
    
    
    
    public static void main(String[] args) {
        // TODO code application logic here
        new ChatClient();
    }
    
}
