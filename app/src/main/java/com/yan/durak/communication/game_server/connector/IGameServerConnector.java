package com.yan.durak.communication.game_server.connector;


import com.yan.durak.gamelogic.communication.protocol.BaseProtocolMessage;

/**
 * Created by Yan-Home on 1/25/2015.
 * This interface is responsible for easy communication between server and a client.
 */
public interface IGameServerConnector {

    /**
     * Listener will be notified of different server events
     */
    public interface IGameServerCommunicatorListener {

        /**
         * New message from the server received.
         */
        void handleServerMessage(BaseProtocolMessage serverMessage);
    }

    /**
     * Connects to remote server
     */
    void connect();

    /**
     * Disconnects from remote server
     */
    void disconnect();

    /**
     * Call this function every frame , to poll any incoming messages
     * @param deltaTimeSeconds time elapsed since last frame
     */
    void update(float deltaTimeSeconds);

    /**
     * Subscribe as a listener to server events.
     */
    void setListener(IGameServerCommunicatorListener listener);

    /**
     * Send a message to remote server
     */
    void sentMessageToServer(BaseProtocolMessage message);

}
