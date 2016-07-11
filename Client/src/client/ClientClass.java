package client;

import deck.Card;

import java.io.*;
import java.net.*;
import java.io.*;
import java.util.Scanner;


public class ClientClass{

    private Socket socket; // pentru conectare la server
    // informatii pt socket
    private int port ;
    private String ip;
    private ObjectInputStream input; // pentru a primi mesase de la server
    private ObjectOutputStream output; // pentru a trimite mesaje la server

    private volatile Object messageReceived;
    private String messageSent;


    ClientClass(String ipToGet, int portToGet)
    {
        port = portToGet;
        ip = ipToGet;
    }

    public void connect()
    {
        try {
            socket = new Socket(ip, port);
            System.out.println("Server connection done");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUpStreams() {

        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush();
            input = new ObjectInputStream(socket.getInputStream());
            System.out.println("The streams are set");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startReading()
    {
        final Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try {
                    messageReceived = input.readObject();
                    while(messageReceived != null)
                    {
                        if (messageReceived instanceof Card)
                        {
                            System.out.println((Card) messageReceived);
                        }
                        if (messageReceived instanceof Integer)
                        {
                            System.out.println((Integer) messageReceived);
                        }
                        if (messageReceived instanceof String)
                        {
                            String message = messageReceived.toString();
                            if (message.equals("BUSTED"))
                            {
                                System.out.println("Bust! You Lost");
                                break;
                            }
                            else if (message.equals("You Win") || message.equals("You Lost") || message.equals("Draw") || message.equals("Dealer BUSTED! You Win!"))
                            {
                                System.out.println(message);
                                break;
                            }
                            else
                            {
                                System.out.println(message);
                            }
                        }
                        messageReceived = input.readObject();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    close();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    close();
                }
                close();
            }
        });
        thread.start();
    }

    public void writeMessage()
    {
        Scanner scan = new Scanner(System.in);
        while(true)
        {
            if(messageReceived instanceof String)
            {
                String message = messageReceived.toString();
                if(message.equals("Enter option: HIT/STAND"))
                {
                    messageSent = scan.next();
                    try {
                        output.writeObject(messageSent);
                    } catch (IOException e) {
                        e.printStackTrace();
                        close();
                    }
                }

                if(messageReceived.equals("BUSTED") || messageReceived.equals("Please wait for the results"))
                    break;

            }
        }
    }

    public void play()
    {
        connect();
        setUpStreams();
        startReading();
        writeMessage();
    }

    public void close()
    {
        try {
            input.close();
            output.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
