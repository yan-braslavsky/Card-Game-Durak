package com.yan.durak.msg_processor.subprocessors.impl;

import com.yan.durak.communication.sender.GameServerMessageSender;
import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.communication.protocol.messages.RequestCardForAttackMessage;
import com.yan.durak.managers.PileManager;
import com.yan.durak.models.PileModel;
import com.yan.durak.msg_processor.subprocessors.BaseMsgSubProcessor;

import java.util.Iterator;

/**
 * Created by ybra on 17/04/15.
 */
public class RequestCardForAttackMsgSubProcessor extends BaseMsgSubProcessor<RequestCardForAttackMessage> {

    private final PileManager mPileManager;
    private final GameServerMessageSender mMessageSender;

    public RequestCardForAttackMsgSubProcessor(final PileManager pileManager, final GameServerMessageSender messageSender) {
        super();
        this.mPileManager = pileManager;
        this.mMessageSender = messageSender;
    }

    @Override
    public void processMessage(RequestCardForAttackMessage serverMessage) {

        PileModel bottomPlayerPile = mPileManager.getBottomPlayerPile();
        if (bottomPlayerPile.getCardsInPile().isEmpty())
            return;

        //TODO : this is just a mock move
        Iterator<Card> iterator = bottomPlayerPile.getCardsInPile().iterator();
        Card cardForAttack = iterator.next();

        //we can just send the response
        mMessageSender.sendCardForAttackResponse(cardForAttack);
    }
}
