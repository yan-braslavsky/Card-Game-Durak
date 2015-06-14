package com.yan.durak.communication.game_server.connector.filter.filter_states.states;

import com.yan.durak.communication.game_server.connector.filter.filter_states.BFBaseState;
import com.yan.durak.gamelogic.communication.protocol.BaseProtocolMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.CardMovedProtocolMessage;

/**
 * Created by Yan-Home on 4/3/2015.
 * <p/>
 * Implemented as singleton to reduce performance impact of continuous allocations.
 */
public class BFCardMsgReceivedState extends BFBaseState {


    public BFCardMsgReceivedState() {
        //must have a public constructor
    }

    @Override
    public void processNextMessageInQueue() {

        //peek at current message
        BaseProtocolMessage currentMessage = mBatchFilter.getIncomingMessagesQueue().peek();

        if (currentMessage == null)
            return;

        if (currentMessage.getMessageName().equals(CardMovedProtocolMessage.MESSAGE_NAME)) {

            //card message received
            //get previous card message
            CardMovedProtocolMessage previousCardMessage = mBatchFilter.getBatchedMessages().get(mBatchFilter.getBatchedMessages().size() - 1);

            //pool message and handle
            handleCardMessageReceived(previousCardMessage, (CardMovedProtocolMessage) currentMessage);
        } else {
            //non card message received
            goToNextState(BFReleaseAndWaitState.class);
        }
    }

    private void handleCardMessageReceived(CardMovedProtocolMessage previousMessage, CardMovedProtocolMessage currentMessage) {

        //cache previous values
        int previousToIndex = previousMessage.getMessageData().getToPileIndex();

        //cache current values
        int currentToIndex = currentMessage.getMessageData().getToPileIndex();

        //cache equality
        boolean sameDestination = (previousToIndex == currentToIndex);

        //decide what state to go next
        if (sameDestination) {
            //poll add current message to batch queue
            mBatchFilter.getBatchedMessages().add((CardMovedProtocolMessage) mBatchFilter.getIncomingMessagesQueue().poll());
        } else {
            goToNextState(BFReleaseAndWaitState.class);
        }
    }
}
