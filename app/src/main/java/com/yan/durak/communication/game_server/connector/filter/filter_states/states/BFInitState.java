package com.yan.durak.communication.game_server.connector.filter.filter_states.states;

import com.yan.durak.communication.game_server.connector.filter.filter_states.BFBaseState;
import com.yan.durak.gamelogic.communication.protocol.BaseProtocolMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.CardMovedProtocolMessage;

/**
 * Created by Yan-Home on 4/3/2015.
 * <p/>
 * Implemented as singleton to reduce performance impact of continuous allocations.
 */
public class BFInitState extends BFBaseState {

    public BFInitState() {
        //must have a public constructor
    }

    @Override
    public void processNextMessageInQueue() {

        //poll the message
        BaseProtocolMessage serverMessage = mBatchFilter.getIncomingMessagesQueue().poll();

        if (serverMessage == null)
            return;

        //proceed to the next state according to message type
        if (serverMessage.getMessageName().equals(CardMovedProtocolMessage.MESSAGE_NAME)) {

            //add received card message to the batched queue
            mBatchFilter.getBatchedMessages().add((CardMovedProtocolMessage) serverMessage);

            //go to next state
            goToNextState(BFCardMsgReceivedState.class);
        } else {

            //poll message and send it to the client
            mBatchFilter.sendMessageToClient(serverMessage);
        }
    }
}