package com.yan.durak.msg_processor.subprocessors.impl;

import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.communication.protocol.messages.CardMovedProtocolMessage;
import com.yan.durak.msg_processor.MsgProcessor;
import com.yan.durak.msg_processor.subprocessors.BaseMsgSubProcessor;

/**
 * Created by ybra on 17/04/15.
 */
public class CardMovedMsgSubProcessor extends BaseMsgSubProcessor<CardMovedProtocolMessage> {

    public CardMovedMsgSubProcessor(MsgProcessor mMsgProcessor) {
        super(mMsgProcessor);
    }

    @Override
    public void processMessage(CardMovedProtocolMessage serverMessage) {
        //extract data
        Card movedCard = new Card(serverMessage.getMessageData().getMovedCard().getRank(), serverMessage.getMessageData().getMovedCard().getSuit());
        int fromPile = serverMessage.getMessageData().getFromPileIndex();
        int toPile = serverMessage.getMessageData().getToPileIndex();

        //FIXME : Do not delegate this logic to the fragment
        //Rather make the logic here ...
        //execute the move
        mMsgProcessor.getPrototypeGameScreen().getCardsScreenFragment().moveCardFromPileToPile(movedCard, fromPile, toPile);
    }
}
