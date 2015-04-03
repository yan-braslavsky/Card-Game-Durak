package com.yan.durak.communication.game_server.connector.filter.filter_states.states;

import com.yan.durak.communication.game_server.connector.filter.CardMoveBatchMessageFilter;
import com.yan.durak.communication.game_server.connector.filter.filter_states.BFBaseState;

/**
 * Created by Yan-Home on 4/3/2015.
 *
 * Implemented as singleton to reduce performance impact of continuous allocations.
 */
public class BFReleaseAndWaitState extends BFBaseState {

    private static BFReleaseAndWaitState INSTANCE;

    public static BFReleaseAndWaitState getInstance(CardMoveBatchMessageFilter cardMoveBatchMessageFilter) {
        if (INSTANCE == null) {
            INSTANCE = new BFReleaseAndWaitState(cardMoveBatchMessageFilter);
        }
        return INSTANCE;
    }

    protected BFReleaseAndWaitState(CardMoveBatchMessageFilter batchFilter) {
        super(batchFilter);
    }

    @Override
    public void processNextMessageInQueue() {

        //release all card messages in queue
        mBatchFilter.releaseBatchedMessages();

        //go to waiting state
        mBatchFilter.setBatchFilterState(BFWaitingState.getInstance(mBatchFilter));
    }
}
