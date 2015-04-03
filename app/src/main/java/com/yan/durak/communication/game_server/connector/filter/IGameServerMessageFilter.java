package com.yan.durak.communication.game_server.connector.filter;

import com.yan.durak.communication.game_server.connector.IGameServerConnector;

/**
 * Created by Yan-Home on 4/2/2015.
 * Filter will manipulate messages according to predefined logic
 */
public interface IGameServerMessageFilter extends IGameServerConnector.IGameServerCommunicatorListener {

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
