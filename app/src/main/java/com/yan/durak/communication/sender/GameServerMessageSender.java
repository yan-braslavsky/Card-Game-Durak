package com.yan.durak.communication.sender;

import com.yan.durak.communication.game_server.connector.IGameServerConnector;

/**
 * Created by ybra on 20/04/15.
 * <p/>
 * PURPOSE :
 * Responsible for sending messages to game server
 */
public class GameServerMessageSender {

    private IGameServerConnector mConnector;

    public GameServerMessageSender(IGameServerConnector gameServerConnector) {
        mConnector = gameServerConnector;
    }
}
