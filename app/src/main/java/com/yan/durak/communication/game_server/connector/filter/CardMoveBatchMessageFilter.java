package com.yan.durak.communication.game_server.connector.filter;

import com.yan.durak.communication.game_server.connector.IGameServerConnector;
import com.yan.durak.communication.game_server.connector.filter.filter_states.IBatchFilterState;
import com.yan.durak.communication.game_server.connector.filter.filter_states.states.BFInitState;
import com.yan.durak.gamelogic.communication.protocol.BaseProtocolMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.CardMovedProtocolMessage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import glengine.yan.glengine.util.object_pool.YANObjectPool;

/**
 * Created by Yan-Home on 4/2/2015.
 * <p/>
 * This filter will combine together messages about cards that moving from same origin pile
 * to the same destination pile.
 * <p/>
 * Once different card move message will arrive , small delay will be used before dispatching
 * a new card movement message.
 */
public class CardMoveBatchMessageFilter implements IGameServerMessageFilter {


    private IGameServerConnector.IGameServerCommunicatorListener mWrappedServerListener;
    private ArrayList<CardMovedProtocolMessage> mBatchedMessages;
    private Queue<BaseProtocolMessage> mIncomingMessagesQueue;
    private IBatchFilterState mBatchFilterState;

    public CardMoveBatchMessageFilter() {
        mBatchedMessages = new ArrayList<>();
        mIncomingMessagesQueue = new LinkedList<>();

        final BFInitState initState = YANObjectPool.getInstance().obtain(BFInitState.class);
        initState.resetState();
        initState.setBatchFilter(this);
        setBatchFilterState(initState);
    }

    public void releaseBatchedMessages() {
        for (final CardMovedProtocolMessage batchedMessage : mBatchedMessages) {
            //release message to the client
            sendMessageToClient(batchedMessage);
        }
        //empty batched messages queue
        mBatchedMessages.clear();
    }

    public void sendMessageToClient(final BaseProtocolMessage batchedMessage) {
        if (mWrappedServerListener != null) {
            mWrappedServerListener.handleServerMessage(batchedMessage);
        }
    }

    @Override
    public void handleServerMessage(final BaseProtocolMessage serverMessage) {
        //put in the queue for later processing
        mIncomingMessagesQueue.add(serverMessage);
    }

    @Override
    public void setWrappedServerListener(final IGameServerConnector.IGameServerCommunicatorListener wrappedServerListener) {
        mWrappedServerListener = wrappedServerListener;
    }

    @Override
    public void update(final float deltaTimeSeconds) {
        mBatchFilterState.processNextMessageInQueue();
    }

    public void setBatchFilterState(final IBatchFilterState batchFilterState) {
        mBatchFilterState = batchFilterState;
        mBatchFilterState.applyState();
    }

    public ArrayList<CardMovedProtocolMessage> getBatchedMessages() {
        return mBatchedMessages;
    }

    public Queue<BaseProtocolMessage> getIncomingMessagesQueue() {
        return mIncomingMessagesQueue;
    }
}