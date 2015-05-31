package com.yan.durak.communication.game_server.connector;

import com.google.gson.Gson;
import com.yan.durak.communication.game_server.connector.filter.CardMoveBatchMessageFilter;
import com.yan.durak.communication.game_server.connector.filter.IGameServerMessageFilter;
import com.yan.durak.gamelogic.communication.protocol.BaseProtocolMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.CardMovedProtocolMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.GameOverProtocolMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.GameSetupProtocolMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.PlayerTakesActionMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.RequestCardForAttackMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.RequestRetaliatePilesMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.RequestThrowInsMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.RetaliationInvalidProtocolMessage;
import com.yan.durak.protocol.messages.BlankProtocolMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yan-Home on 4/2/2015.
 */
public abstract class BaseGameServerConnector implements IGameServerConnector {

    //used for easy access to class of the message by it's name
    private Map<String, Class<? extends BaseProtocolMessage>> mNamesToClassMap;

    //used to preprocess the message before it get dispatched to the client
    private IGameServerMessageFilter mMessageFilter;

    private Gson mGson;

    protected BaseGameServerConnector() {
        mGson = new Gson();
        mNamesToClassMap = new HashMap<>();

        //we are applying batch filter on incoming messages
        //certain messages will be batched together and dispatched
        //while others will be hold
        mMessageFilter = new CardMoveBatchMessageFilter();

        //put all message classes and their names into a map
        fillNameToClassMap();

    }

    private void fillNameToClassMap() {
        mNamesToClassMap.put(CardMovedProtocolMessage.MESSAGE_NAME, CardMovedProtocolMessage.class);
        mNamesToClassMap.put(RequestCardForAttackMessage.MESSAGE_NAME, RequestCardForAttackMessage.class);
        mNamesToClassMap.put(RequestRetaliatePilesMessage.MESSAGE_NAME, RequestRetaliatePilesMessage.class);
        mNamesToClassMap.put(GameSetupProtocolMessage.MESSAGE_NAME, GameSetupProtocolMessage.class);
        mNamesToClassMap.put(PlayerTakesActionMessage.MESSAGE_NAME, PlayerTakesActionMessage.class);
        mNamesToClassMap.put(RetaliationInvalidProtocolMessage.MESSAGE_NAME, RetaliationInvalidProtocolMessage.class);
        mNamesToClassMap.put(RequestThrowInsMessage.MESSAGE_NAME, RequestThrowInsMessage.class);
        mNamesToClassMap.put(GameOverProtocolMessage.MESSAGE_NAME, GameOverProtocolMessage.class);
        //TODO : add more protocol message classes as they will be added...
    }

    @Override
    public void update(float deltaTimeSeconds) {
        //read messages from remote socket server
        readMessageFromServer();

        //update the filter
        mMessageFilter.update(deltaTimeSeconds);
    }

    private void readMessageFromServer() {

        //we are reading messages only if the connector is currently connected
        if (!SocketConnectionManager.getInstance().isConnected())
            return;

        //try to obtain message from the connector
        String msg = SocketConnectionManager.getInstance().readMessageFromRemoteServer();

        //in case there was no message or no listener to process it ,we will do nothing
        if (msg == null || mMessageFilter == null)
            return;

        //handle the message
        handleServerMessage(msg);
    }

    private void handleServerMessage(String msg) {
        //read the message as blank , to identify its name
        BlankProtocolMessage message = mGson.fromJson(msg, BlankProtocolMessage.class);

        //forward the actual message to listener
        mMessageFilter.handleServerMessage(mGson.fromJson(msg, mNamesToClassMap.get(message.getMessageName())));
    }

    @Override
    public void setListener(IGameServerCommunicatorListener listener) {
        mMessageFilter.setWrappedServerListener(listener);
    }
}
