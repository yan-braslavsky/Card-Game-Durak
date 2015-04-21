package com.yan.durak.msg_processor.subprocessors.impl;

import com.yan.durak.gamelogic.communication.protocol.messages.RequestCardForAttackMessage;
import com.yan.durak.msg_processor.subprocessors.BaseMsgSubProcessor;

/**
 * Created by ybra on 17/04/15.
 */
public class RequestCardForAttackMsgSubProcessor extends BaseMsgSubProcessor<RequestCardForAttackMessage> {

    public RequestCardForAttackMsgSubProcessor() {
        super();
    }

    @Override
    public void processMessage(RequestCardForAttackMessage serverMessage) {

        //FIXME : Do not store this info on the screen
        //rather transition to other processor state
//        mMsgProcessor.getPrototypeGameScreen().getGameSession().setActivePlayerState(ActivePlayerState.REQUEST_CARD_FOR_ATTACK);
    }
}
