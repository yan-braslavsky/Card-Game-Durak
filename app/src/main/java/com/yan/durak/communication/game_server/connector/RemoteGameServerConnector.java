package com.yan.durak.communication.game_server.connector;

import com.yan.durak.communication.socket.SocketConnectionManager;
import com.yan.durak.gamelogic.communication.protocol.BaseProtocolMessage;

/**
 * Created by Yan-Home on 1/25/2015.
 * <p/>
 * This class is responsible to handle communication between server and client in an easy manner.
 */
public class RemoteGameServerConnector extends BaseGameServerConnector {

    private static final String SERVER_ADDRESS = "192.168.1.101";
    private static final int SERVER_PORT = 7000;

    public RemoteGameServerConnector() {
        super();
    }

    @Override
    public void connect() {
        SocketConnectionManager.getInstance().connectToRemoteServer(SERVER_ADDRESS, SERVER_PORT);
    }

    @Override
    public void disconnect() {
        SocketConnectionManager.getInstance().disconnectFromRemoteServer();
    }

    @Override
    public void sentMessageToServer(BaseProtocolMessage message) {
        SocketConnectionManager.getInstance().sendMessageToRemoteServer(message.toJsonString());
    }
}
