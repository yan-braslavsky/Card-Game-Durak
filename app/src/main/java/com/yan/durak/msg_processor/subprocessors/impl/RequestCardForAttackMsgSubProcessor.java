package com.yan.durak.msg_processor.subprocessors.impl;

import com.yan.durak.gamelogic.communication.protocol.messages.RequestCardForAttackMessage;
import com.yan.durak.msg_processor.MsgProcessor;
import com.yan.durak.msg_processor.subprocessors.BaseMsgSubProcessor;

/**
 * Created by ybra on 17/04/15.
 */
public class RequestCardForAttackMsgSubProcessor extends BaseMsgSubProcessor<RequestCardForAttackMessage> {

    public RequestCardForAttackMsgSubProcessor(MsgProcessor mMsgProcessor) {
        super(mMsgProcessor);
    }

    @Override
    public void processMessage(RequestCardForAttackMessage serverMessage) {

        //FIXME : Do not store this info on the screen
        //rather transition to other processor state
        mMsgProcessor.getPrototypeGameScreen().getGameSession().setCardForAttackRequested(true);
    }
}
