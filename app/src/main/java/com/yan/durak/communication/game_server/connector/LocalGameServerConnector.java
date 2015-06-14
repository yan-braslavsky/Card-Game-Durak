package com.yan.durak.communication.game_server.connector;

import com.yan.durak.gamelogic.communication.protocol.BaseProtocolMessage;

import glengine.yan.glengine.service.ServiceLocator;

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
        ServiceLocator.locateService(SocketConnectionManager.class).connectToLocalServer();
    }

    @Override
    public void disconnect() {
        ServiceLocator.locateService(SocketConnectionManager.class).disconnectFromLocalServer();
    }

    @Override
    public void sentMessageToServer(BaseProtocolMessage message) {
        ServiceLocator.locateService(SocketConnectionManager.class).sendMessageToRemoteServer(message.toJsonString());
    }
}