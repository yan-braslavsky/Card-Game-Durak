package com.yan.durak.msg_processor.subprocessors;

import com.yan.durak.gamelogic.communication.protocol.BaseProtocolMessage;

/**
 * Created by ybra on 17/04/15.
 * PURPOSE :
 * Defines an interface of processor that handles single message processing
 */
public interface MsgSubProcessor<T extends BaseProtocolMessage> {

    /**
     * Concrete message to be processed
     */
    void processMessage(T serverMessage);
}
