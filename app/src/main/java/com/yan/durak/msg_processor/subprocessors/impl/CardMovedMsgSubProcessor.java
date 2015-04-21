package com.yan.durak.msg_processor.subprocessors.impl;

import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.communication.protocol.messages.CardMovedProtocolMessage;
import com.yan.durak.layouting.pile.IPileLayouter;
import com.yan.durak.managers.PileLayouterManager;
import com.yan.durak.managers.PileManager;
import com.yan.durak.models.PileModel;
import com.yan.durak.msg_processor.subprocessors.BaseMsgSubProcessor;

/**
 * Created by ybra on 17/04/15.
 */
public class CardMovedMsgSubProcessor extends BaseMsgSubProcessor<CardMovedProtocolMessage> {

    private final PileManager mPileManager;
    private final PileLayouterManager mPileLayouterManager;

    public CardMovedMsgSubProcessor(final PileManager mPileManager, final PileLayouterManager mPileLayouterManager) {
        super();

        this.mPileManager = mPileManager;
        this.mPileLayouterManager = mPileLayouterManager;
    }

    @Override
    public void processMessage(CardMovedProtocolMessage serverMessage) {

        //get the indexes from the message
        final int fromPileIndex = serverMessage.getMessageData().getFromPileIndex();
        final int toPileIndex = serverMessage.getMessageData().getToPileIndex();

        //Pile is a local representation of what is going on on the server
        final PileModel fromPile = mPileManager.getPileWithIndex(fromPileIndex);

        //get the card that is about to be moved
        final Card movedCard = fromPile.getCardByRankAndSuit(serverMessage.getMessageData().getMovedCard().getRank(), serverMessage.getMessageData().getMovedCard().getSuit());

        //make sure that the card that we want to move is actually in the pile
        if (movedCard == null) {
            throw new RuntimeException("The card " + movedCard + "is not found in pile");
        }

        //Pile is a local representation of what is going on on the server
        final PileModel toPile = mPileManager.getPileWithIndex(toPileIndex);

        //remove the card from the pile and place into the other
        fromPile.removeCard(movedCard);
        toPile.addCard(movedCard);

        //we need to update both piles that had changes
        final IPileLayouter fromPileLayouter = mPileLayouterManager.getPileLayouterForPile(fromPile);
        final IPileLayouter toPileLayouter = mPileLayouterManager.getPileLayouterForPile(toPile);

        //make the actual layout
        fromPileLayouter.layout();
        toPileLayouter.layout();

    }
}
