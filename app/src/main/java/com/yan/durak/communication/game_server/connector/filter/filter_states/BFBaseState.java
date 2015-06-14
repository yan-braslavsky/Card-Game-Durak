package com.yan.durak.communication.game_server.connector.filter.filter_states;

import com.yan.durak.communication.game_server.connector.filter.CardMoveBatchMessageFilter;

import glengine.yan.glengine.util.object_pool.YANIPoolableObject;
import glengine.yan.glengine.util.object_pool.YANObjectPool;

/**
 * Created by Yan-Home on 4/3/2015.
 */
public abstract class BFBaseState implements IBatchFilterState, YANIPoolableObject {

    protected CardMoveBatchMessageFilter mBatchFilter;

    public void setBatchFilter(CardMoveBatchMessageFilter batchFilter) {
        mBatchFilter = batchFilter;
    }

    @Override
    public void applyState() {
        //Can be overridden
    }

    @Override
    public void resetState() {
        mBatchFilter = null;
    }

    /**
     * Goes to next state and pools the current
     */
    protected <T extends BFBaseState> void goToNextState(Class<T> nextStateClazz) {
        CardMoveBatchMessageFilter cachedBatchFilter = mBatchFilter;

        //obtain the next state from pool
        T nextState = YANObjectPool.getInstance().obtain(nextStateClazz);
        nextState.setBatchFilter(cachedBatchFilter);

        //reset this state and offer it to pool
        this.resetState();
        YANObjectPool.getInstance().offer(this);

        //set the next state
        cachedBatchFilter.setBatchFilterState(nextState);
    }
}
