package com.yan.durak.communication.game_server;

import com.yan.durak.communication.socket.SocketConnectionManager;
import com.yan.durak.protocol.BaseProtocolMessage;
import com.yan.durak.protocol.messages.BlankProtocolMessage;
import com.yan.durak.protocol.messages.CardMovedProtocolMessage;
import com.yan.durak.protocol.messages.GameSetupProtocolMessage;
import com.yan.durak.protocol.messages.PlayerTakesActionMessage;
import com.yan.durak.protocol.messages.RequestCardForAttackMessage;
import com.yan.durak.protocol.messages.RequestRetaliatePilesMessage;
import com.yan.durak.protocol.messages.RequestThrowInsMessage;
import com.yan.durak.protocol.messages.RetaliationInvalidProtocolMessage;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yan-Home on 1/25/2015.
 * <p/>
 * This class is responsible to handle communication between local server and client in an easy manner.
 */
public class LocalGameServerCommunicator implements IGameServerConnector {

    private Gson mGson;
    private IGameServerCommunicatorListener mCommunicatorListener;

    //used for easy access to class of the message by it's name
    private Map<String, Class<? extends BaseProtocolMessage>> mNamesToClassMap;

    public LocalGameServerCommunicator() {
        mGson = new Gson();
        mNamesToClassMap = new HashMap<>();

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
        //TODO : add more protocol message classes as they will be added...
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
    public void update() {
        //read messages from remote socket server
        readMessageFromServer();
    }

    @Override
    public void setListener(IGameServerCommunicatorListener listener) {
        mCommunicatorListener = listener;
    }

    @Override
    public void sentMessageToServer(BaseProtocolMessage message) {
        SocketConnectionManager.getInstance().sendMessageToRemoteServer(message.toJsonString());
        //TODO : handle differently
    }

    private void readMessageFromServer() {

        //we are reading messages only if the connector is currently connected
        if (!SocketConnectionManager.getInstance().isConnected())
            return;

        //try to obtain message from the connector
        String msg = SocketConnectionManager.getInstance().readMessageFromRemoteServer();

        //in case there was no message or no listener to process it ,we will do nothing
        if (msg == null || mCommunicatorListener == null)
            return;

        //handle the message
        handleServerMessage(msg);
    }

    private void handleServerMessage(String msg) {
        //read the message as blank , to identify its name
        BlankProtocolMessage message = mGson.fromJson(msg, BlankProtocolMessage.class);

        //forward the actual message to listener
        mCommunicatorListener.handleServerMessage(mGson.fromJson(msg, mNamesToClassMap.get(message.getMessageName())));
    }

}
