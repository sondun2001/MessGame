package com.rebelo.messgame.services;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetworkManager {

    Server server;

    public NetworkManager() {

        // TODO: Define what sort of communication we will be sending to / from server
        // TODO: Emit events. Allow UI / Map, etc to handle
        // TODO: Create client, attempt to find host.
        // TODO: Store in redis here?

        Client client = new Client();
        client.start();

        InetAddress address = client.discoverHost(54777, 5000);
        if (address == null) {
            Gdx.app.log("Network", "Could not find server, starting server.");

            server = new Server();
            server.start();
            try {
                server.bind(54555, 54777);
            } catch (IOException e) {
                Gdx.app.error("Network", "Could not start server.");
            }

            try {
                address = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

        if (address != null) {
            try {
                client.connect(5000, address, 54555, 54777);
            } catch (IOException clientException) {
                Gdx.app.error("Network", "Could not connect to server.");
            }

            client.addListener(new Listener() {
                public void received(Connection connection, Object object) {
                    Gdx.app.log("Client", "Received");
                /*
                if (object instanceof SomeResponse) {
                    SomeResponse response = (SomeResponse)object;
                    System.out.println(response.text);
                }
                */
                }
            });

            /*
            SomeRequest request = new SomeRequest();
            request.text = "Here is the request";
            client.sendTCP(request);
            */


            /*
            Kryo kryo = server.getKryo();
            kryo.register(SomeRequest.class);
            kryo.register(SomeResponse.class);

            kryo = client.getKryo();
            kryo.register(SomeRequest.class);
            kryo.register(SomeResponse.class);
            */
        }
    }
}
