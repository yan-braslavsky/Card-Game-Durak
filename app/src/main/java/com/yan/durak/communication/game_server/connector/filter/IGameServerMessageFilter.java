package com.yan.durak.communication.game_server.connector.filter;

import com.yan.durak.communication.game_server.connector.IGameServerConnector;
import com.yan.durak.gamelogic.communication.protocol.BaseProtocolMessage;

/**
 * Created by Yan-Home on 4/2/2015.
 * Filter will manipulate messages according to predefined logic
 */
public interface IGameServerMessageFilter extends IGameServerConnector.IGameServerCommunicatorListener {

    /**
     * Filter incoming message
     */
    void filterServerMessage(BaseProtocolMessage serverMessage);

    /**
     * Set the communicator listener that client has originally set to the communicator.
     * This listener will be used to dispatch filtered messages.
     */
    void setWrappedServerListener(IGameServerConnector.IGameServerCommunicatorListener wrappedServerListener);

    /**
     * Call this function every frame
     * @param deltaTimeSeconds time elapsed since last frame
     */
    void update(float deltaTimeSeconds);
}
