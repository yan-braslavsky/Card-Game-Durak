package com.yan.durak.communication.game_server.connector.filter.filter_states.states;

import com.yan.durak.communication.game_server.connector.filter.filter_states.BFBaseState;

/**
 * Created by Yan-Home on 4/3/2015.
 * <p/>
 * Implemented as singleton to reduce performance impact of continuous allocations.
 */
public class BFReleaseAndWaitState extends BFBaseState {

    public BFReleaseAndWaitState() {
        //must have a public constructor
    }

    @Override
    public void processNextMessageInQueue() {

        //release all card messages in queue
        mBatchFilter.releaseBatchedMessages();

        //go to waiting state
        goToNextState(BFWaitingState.class);
    }
}
