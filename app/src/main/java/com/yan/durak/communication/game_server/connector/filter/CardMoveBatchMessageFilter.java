package com.yan.durak.communication.game_server.connector.filter;

import com.yan.durak.communication.game_server.connector.IGameServerConnector;
import com.yan.durak.gamelogic.communication.protocol.BaseProtocolMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.CardMovedProtocolMessage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

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

    /**
     * Time that is required to wait between cards movement
     */
    private static final float WAITING_TIME_IN_SECONDS = 5.0f;

    private IGameServerConnector.IGameServerCommunicatorListener mWrappedServerListener;
    private ArrayList<CardMovedProtocolMessage> mBatchedMessages;
    private Queue<BaseProtocolMessage> mIncomingMessagesQueue;

    //markers that help to identify same movement
    private int mLastFromPileIndex;
    private int mLastToPileIndex;
    private float timeToWait;

    public CardMoveBatchMessageFilter() {
        mBatchedMessages = new ArrayList<>();
        mIncomingMessagesQueue = new LinkedList<>();
        timeToWait = 0;

        //reset markers
        resetMarkers();
    }

    private void resetMarkers() {
        mLastFromPileIndex = -1;
        mLastToPileIndex = -1;
    }

    @Override
    public void filterServerMessage(BaseProtocolMessage serverMessage) {
        //batching messages here , and then dispatch to a wrapped listener
        if (serverMessage.getMessageName().equals(CardMovedProtocolMessage.MESSAGE_NAME)) {
            //this is kind of messages that we are filtering
            filterCardMoveMessage((CardMovedProtocolMessage) serverMessage);
        } else {
            //message is different from what we are looking for
            //so just release all batches and the current message too
            releaseBatchedMessages();
            sendMessageToClient(serverMessage);
        }
    }

    private void filterCardMoveMessage(CardMovedProtocolMessage cardMovedMessage) {

        //init markers if needed
        if (mLastFromPileIndex == -1 || mLastToPileIndex == -1) {
            mLastFromPileIndex = cardMovedMessage.getMessageData().getFromPileIndex();
            mLastToPileIndex = cardMovedMessage.getMessageData().getToPileIndex();
        }

        //look into message content
        boolean sameOrigin = cardMovedMessage.getMessageData().getFromPileIndex() == mLastFromPileIndex;
        boolean sameDestination = cardMovedMessage.getMessageData().getToPileIndex() == mLastToPileIndex;

        //in case message has different destanation or origin we
        //reseting the batching by releasing previous messages
        if (!(sameOrigin || sameDestination)) {
            releaseBatchedMessages();
        }

        //add message to batch queue
        mBatchedMessages.add(cardMovedMessage);
    }

    private void releaseBatchedMessages() {

        //reset markers
        resetMarkers();

        if(mBatchedMessages.isEmpty())
            return;

        for (CardMovedProtocolMessage batchedMessage : mBatchedMessages) {
            //release message to the client
            sendMessageToClient(batchedMessage);
        }

        //set waiting time until next message handling
        timeToWait = WAITING_TIME_IN_SECONDS;

        //empty batched messages queue
        mBatchedMessages.clear();

    }

    private void sendMessageToClient(BaseProtocolMessage batchedMessage) {
        if (mWrappedServerListener != null) {
            mWrappedServerListener.handleServerMessage(batchedMessage);
        }
    }

    @Override
    public void handleServerMessage(BaseProtocolMessage serverMessage) {
        //put in the queue for later processing
        mIncomingMessagesQueue.add(serverMessage);
    }

    @Override
    public void setWrappedServerListener(IGameServerConnector.IGameServerCommunicatorListener wrappedServerListener) {
        mWrappedServerListener = wrappedServerListener;
    }

    @Override
    public void update(float deltaTimeSeconds) {

        updateTimeCounter(deltaTimeSeconds);

        //we will not process the next message as long as
        //the time to wait is not over
        if(timeToWait > 0)
            return;

        BaseProtocolMessage message = mIncomingMessagesQueue.poll();

        if (message == null)
            return;

        //go through filtering
        filterServerMessage(message);
    }

    private void updateTimeCounter(float deltaTimeSeconds) {
        timeToWait -=deltaTimeSeconds;

        //we are fixing the time value , in case it
        //will get too low. Taking buffer of 5 seconds.
        if(timeToWait < (Float.MIN_VALUE + 5)){
            timeToWait = 0;
        }
    }
}