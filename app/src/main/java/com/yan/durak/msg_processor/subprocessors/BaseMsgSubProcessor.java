package com.yan.durak.msg_processor.subprocessors;

import com.yan.durak.gamelogic.communication.protocol.BaseProtocolMessage;
import com.yan.durak.msg_processor.MsgProcessor;

/**
 * Created by ybra on 17/04/15.
 */
public abstract class BaseMsgSubProcessor<T extends BaseProtocolMessage> implements MsgSubProcessor<T> {

//    protected MsgProcessor mMsgProcessor;
    protected BaseMsgSubProcessor() {
//        this.mMsgProcessor = mMsgProcessor;
    }
}
