package com.yan.durak.communication.game_server.connector.filter.filter_states;

/**
 * Created by Yan-Home on 4/3/2015.
 * <p/>
 * States define how to handle incoming messages.
 * The logic is complex , therefore it is architectured as a stat machine.
 */
public interface IBatchFilterState {

    /**
     * Called every frame
     */
    void processNextMessageInQueue();

    /**
     * Called immediately when  state is set
     */
    void applyState();
}
