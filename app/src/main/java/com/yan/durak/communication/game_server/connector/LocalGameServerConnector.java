package com.yan.durak.communication.game_server.connector;

import com.yan.durak.gamelogic.communication.protocol.BaseProtocolMessage;

/**
 * Created by Yan-Home on 1/25/2015.
 * <p/>
 * This class is responsible to handle communication between local server and client in an easy manner.
 */
public class LocalGameServerConnector extends BaseGameServerConnector {

    public LocalGameServerConnector() {
        super();
    }

    @Override
    public void connect() {
        SocketConnectionManager.getInstance().connectToLocalServer();
    }

    @Override
    public void disconnect() {
        SocketConnectionManager.getInstance().disconnectFromLocalServer();
    }

    @Override
    public void sentMessageToServer(BaseProtocolMessage message) {
        SocketConnectionManager.getInstance().sendMessageToRemoteServer(message.toJsonString());
    }
}