package com.yan.durak.msg_processor.states;

import com.yan.durak.msg_processor.MsgProcessor;

/**
 * Created by ybra on 16/04/15.
 *
 * PURPOSE :
 * Holds a common functionality for all states.
 */
public abstract class MsgProcessorBaseState implements MsgProcessorState {

    protected MsgProcessor mMsgProcessor;

    protected MsgProcessorBaseState(MsgProcessor mMsgProcessor) {
        this.mMsgProcessor = mMsgProcessor;
    }
}
