package com.yan.durak.msg_processor.subprocessors.impl;

import com.yan.durak.communication.sender.GameServerMessageSender;
import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.communication.protocol.messages.RequestThrowInsMessage;
import com.yan.durak.msg_processor.subprocessors.BaseMsgSubProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ybra on 17/04/15.
 */
public class RequestThrowInsMsgSubProcessor extends BaseMsgSubProcessor<RequestThrowInsMessage> {


    private final GameServerMessageSender mMessageSender;
    private final List<Card> mThrowInCardList;

    public RequestThrowInsMsgSubProcessor(final GameServerMessageSender messageSender) {
        super();

        this.mMessageSender = messageSender;
        this.mThrowInCardList = new ArrayList<>();
    }

    @Override
    public void processMessage(RequestThrowInsMessage serverMessage) {
        //TODO : for now just return empty array
        mMessageSender.sendThrowInResponse(mThrowInCardList);
    }
}
