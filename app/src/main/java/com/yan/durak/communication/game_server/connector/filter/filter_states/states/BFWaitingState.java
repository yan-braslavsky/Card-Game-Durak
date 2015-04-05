package com.yan.durak.communication.game_server.connector.filter.filter_states.states;

import com.yan.durak.communication.game_server.connector.filter.CardMoveBatchMessageFilter;
import com.yan.durak.communication.game_server.connector.filter.filter_states.BFBaseState;

import glengine.yan.glengine.tasks.YANDelayedTask;
import glengine.yan.glengine.util.object_pool.YANIPoolableObject;
import glengine.yan.glengine.util.object_pool.YANObjectPool;

/**
 * Created by Yan-Home on 4/3/2015.
 * <p/>
 * Implemented as singleton to reduce performance impact of continuous allocations.
 */
public class BFWaitingState extends BFBaseState {

    public static final float DURATION_SECONDS = 0.6f;
    public static final int COUNT_OF_PREALLOCATED_OBJECTS = 6;
    private static BFWaitingState INSTANCE;

    public static BFWaitingState getInstance(CardMoveBatchMessageFilter cardMoveBatchMessageFilter) {
        if (INSTANCE == null) {
            INSTANCE = new BFWaitingState(cardMoveBatchMessageFilter);

            //preallocate delayed tasks in the object pool
            YANObjectPool.getInstance().preallocate(YANDelayedTask.class, COUNT_OF_PREALLOCATED_OBJECTS);
            YANObjectPool.getInstance().preallocate(BFWaitingStateDelayedTaskListener.class, COUNT_OF_PREALLOCATED_OBJECTS);
        }
        return INSTANCE;
    }

    protected BFWaitingState(CardMoveBatchMessageFilter batchFilter) {
        super(batchFilter);
    }

    @Override
    public void applyState() {

        //Every time we are at this state we need to start a new task

        //obtain objects from an object pool
        YANDelayedTask delayedTask = YANObjectPool.getInstance().obtain(YANDelayedTask.class);
        BFWaitingStateDelayedTaskListener delayedTaskListener = YANObjectPool.getInstance().obtain(BFWaitingStateDelayedTaskListener.class);

        //init the listener
        delayedTaskListener.setBatchFilter(mBatchFilter);
        delayedTaskListener.setDelayedTask(delayedTask);

        //init the task
        delayedTask.setDurationSeconds(DURATION_SECONDS);
        delayedTask.setDelayedTaskListener(delayedTaskListener);

        //start the task
        delayedTask.start();
    }

    @Override
    public void processNextMessageInQueue() {
        //Does nothing
    }

    /**
     * We retaining this class to not create many instances of it
     */
    protected static class BFWaitingStateDelayedTaskListener implements YANDelayedTask.YANDelayedTaskListener, YANIPoolableObject {
        private CardMoveBatchMessageFilter mBatchFilter;
        private YANDelayedTask mDelayedTask;

        public BFWaitingStateDelayedTaskListener() {
            //Empty constructor required
        }

        @Override
        public void onComplete() {

            //we are caching the instance , because after recycling it will go away
            CardMoveBatchMessageFilter cachedFilter = mBatchFilter;

            //recycle delayed task
            YANObjectPool.getInstance().offer(mDelayedTask);

            //recycle this listener
            YANObjectPool.getInstance().offer(BFWaitingStateDelayedTaskListener.this);

            //go back to initial state
            cachedFilter.setBatchFilterState(BFInitState.getInstance(cachedFilter));
        }

        @Override
        public void resetState() {
            mBatchFilter = null;
            mDelayedTask = null;
        }

        public void setBatchFilter(CardMoveBatchMessageFilter batchFilter) {
            mBatchFilter = batchFilter;
        }

        public void setDelayedTask(YANDelayedTask delayedTask) {
            mDelayedTask = delayedTask;
        }
    }
}
