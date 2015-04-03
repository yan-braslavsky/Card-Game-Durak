package com.yan.durak.communication.game_server.connector.filter.filter_states;

import com.yan.durak.communication.game_server.connector.filter.CardMoveBatchMessageFilter;

/**
 * Created by Yan-Home on 4/3/2015.
 */
public abstract class BFBaseState implements IBatchFilterState {

    protected CardMoveBatchMessageFilter mBatchFilter;
    protected BFBaseState(CardMoveBatchMessageFilter batchFilter) {
        mBatchFilter = batchFilter;
    }

    @Override
    public void applyState() {
        //Can be overridden
    }
}
