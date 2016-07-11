package client;

import client.ClientClass;

import java.io.IOException;

public class MainClass {
    public static void main (String[] argc)
    {
        ClientClass client = new ClientClass("188.25.25.216", 9797); // momentan ar trebui sa fim conectati
        client.play();
    }
}
