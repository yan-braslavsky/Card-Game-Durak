package com.yan.durak.communication.game_server.connector.filter.filter_states.states;

import com.yan.durak.communication.game_server.connector.filter.CardMoveBatchMessageFilter;
import com.yan.durak.communication.game_server.connector.filter.filter_states.BFBaseState;

import glengine.yan.glengine.tasks.YANDelayedTask;
import glengine.yan.glengine.tasks.YANTaskManager;

/**
 * Created by Yan-Home on 4/3/2015.
 * <p/>
 * Implemented as singleton to reduce performance impact of continuous allocations.
 */
public class BFWaitingState extends BFBaseState {

    public static final float DURATION_SECONDS = 0.6f;
    private static BFWaitingState INSTANCE;
    private YANDelayedTask mDelayedTask;

    public static BFWaitingState getInstance(CardMoveBatchMessageFilter cardMoveBatchMessageFilter) {
        if (INSTANCE == null) {
            INSTANCE = new BFWaitingState(cardMoveBatchMessageFilter);
        }
        return INSTANCE;
    }

    protected BFWaitingState(CardMoveBatchMessageFilter batchFilter) {
        super(batchFilter);
        mDelayedTask = new YANDelayedTask(DURATION_SECONDS, new BFWaitingStateDelayedTaskListener(batchFilter));
    }

    @Override
    public void applyState() {
        YANTaskManager.getInstance().addTask(mDelayedTask);
    }

    @Override
    public void processNextMessageInQueue() {
        //Does nothing
    }

    /**
     * We retaining this class to not create many instances of it
     */
    private class BFWaitingStateDelayedTaskListener implements YANDelayedTask.YANDelayedTaskListener {
        private CardMoveBatchMessageFilter mBatchFilter;

        private BFWaitingStateDelayedTaskListener(CardMoveBatchMessageFilter batchFilter) {
            mBatchFilter = batchFilter;
        }

        @Override
        public void onComplete() {
            //go back to initial state
            mBatchFilter.setBatchFilterState(BFInitState.getInstance(mBatchFilter));
        }
    }
}
