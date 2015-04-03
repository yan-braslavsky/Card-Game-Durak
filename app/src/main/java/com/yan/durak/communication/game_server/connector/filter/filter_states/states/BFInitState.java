package com.yan.durak.communication.game_server.connector.filter.filter_states.states;

import com.yan.durak.communication.game_server.connector.filter.CardMoveBatchMessageFilter;
import com.yan.durak.communication.game_server.connector.filter.filter_states.BFBaseState;
import com.yan.durak.gamelogic.communication.protocol.BaseProtocolMessage;
import com.yan.durak.gamelogic.communication.protocol.messages.CardMovedProtocolMessage;

/**
 * Created by Yan-Home on 4/3/2015.
 *
 * Implemented as singleton to reduce performance impact of continuous allocations.
 */
public class BFInitState extends BFBaseState {

    private static BFInitState INSTANCE;

    public static BFInitState getInstance(CardMoveBatchMessageFilter cardMoveBatchMessageFilter) {
        if(INSTANCE == null){
            INSTANCE = new BFInitState(cardMoveBatchMessageFilter);
        }
        return INSTANCE;
    }

    public BFInitState(CardMoveBatchMessageFilter batchFilter) {
        super(batchFilter);
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
            mBatchFilter.setBatchFilterState(BFCardMsgReceivedState.getInstance(mBatchFilter));
        } else {

            //poll message and send it to the client
            mBatchFilter.sendMessageToClient(serverMessage);
        }
    }
}